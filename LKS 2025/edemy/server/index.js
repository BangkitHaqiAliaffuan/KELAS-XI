const express = require('express');
const mongoose = require('mongoose');
require('dotenv').config();

// Import routes
const coursesRoutes = require('./routes/courses');
const usersRoutes = require('./routes/users');
const testimonialsRoutes = require('./routes/testimonials');
const enrollmentsRoutes = require('./routes/enrollments');

const app = express();
const PORT = 5000;

// Middleware untuk raw body (diperlukan untuk Stripe webhook)
app.use('/api/webhooks/stripe', express.raw({ type: 'application/json' }));

// Simple middleware untuk routes lainnya
app.use(express.json());
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', '*');
  res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  next();
});

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URL)
  .then(() => console.log('✅ MongoDB connected'))
  .catch(err => console.log('❌ MongoDB error:', err.message));

// API Routes
app.use('/api/courses', coursesRoutes);
app.use('/api/users', usersRoutes);
app.use('/api/testimonials', testimonialsRoutes);
app.use('/api/enrollments', enrollmentsRoutes);
app.use('/api/payments', require('./routes/payments'));
app.use('/api/clerk', require('./routes/clerk-integration'));

// Simple routes
app.get('/', (req, res) => {
  res.json({ 
    message: 'Edemy LMS API running!',
    timestamp: new Date().toISOString(),
    endpoints: {
      health: '/health',
      courses: '/api/courses',
      users: '/api/users',
      testimonials: '/api/testimonials',
      enrollments: '/api/enrollments',
      stripe_webhook: '/api/webhooks/stripe'
    }
  });
});

app.get('/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    time: new Date(),
    database: mongoose.connection.readyState === 1 ? 'Connected' : 'Disconnected'
  });
});

// Helper function to handle successful payment
async function handleSuccessfulPayment(paymentIntent) {
  try {
    const Course = require('./models/Course');
    const User = require('./models/User');
    const axios = require('axios');

    const { courseId, userEmail } = paymentIntent.metadata;
    
    if (!courseId || !userEmail) {
      console.log('❌ Missing metadata in payment intent:', paymentIntent.metadata);
      return;
    }

    console.log('🔄 Processing successful payment for:', { courseId, userEmail });

    // Find user by email
    const user = await User.findOne({ email: userEmail });
    if (!user) {
      console.log('❌ User not found:', userEmail);
      return;
    }

    // Get course details
    const course = await Course.findById(courseId);
    if (!course) {
      console.log('❌ Course not found:', courseId);
      return;
    }

    // Create enrollment via API endpoint
    const enrollmentData = {
      clerkId: user.clerkId,
      courseId: courseId,
      paymentIntentId: paymentIntent.id,
      amount: paymentIntent.amount / 100 // Convert from cents
    };

    console.log('📝 Creating enrollment with data:', enrollmentData);

    // Call internal enrollment creation endpoint
    try {
      const response = await axios.post('http://localhost:5000/api/enrollments/create-after-payment', enrollmentData);
      
      if (response.data.success) {
        console.log('✅ Auto-enrollment created from payment intent:', response.data.data._id);
      } else {
        console.log('❌ Failed to create enrollment:', response.data.message);
      }
    } catch (enrollmentError) {
      console.log('❌ Error calling enrollment endpoint:', enrollmentError.message);
    }

  } catch (error) {
    console.error('❌ Error in handleSuccessfulPayment:', error);
  }
}

// Helper function to handle successful checkout
async function handleSuccessfulCheckout(session) {
  try {
    const Course = require('./models/Course');
    const User = require('./models/User');
    const axios = require('axios');

    const { courseId, userEmail } = session.metadata;
    
    if (!courseId || !userEmail) {
      console.log('❌ Missing metadata in checkout session:', session.metadata);
      return;
    }

    console.log('🔄 Processing successful checkout for:', { courseId, userEmail });

    // Find user by email
    const user = await User.findOne({ email: userEmail });
    if (!user) {
      console.log('❌ User not found:', userEmail);
      return;
    }

    // Get course details
    const course = await Course.findById(courseId);
    if (!course) {
      console.log('❌ Course not found:', courseId);
      return;
    }

    // Create enrollment via API endpoint
    const enrollmentData = {
      clerkId: user.clerkId,
      courseId: courseId,
      paymentIntentId: session.payment_intent,
      sessionId: session.id,
      amount: session.amount_total / 100 // Convert from cents
    };

    console.log('📝 Creating enrollment from checkout with data:', enrollmentData);

    // Call internal enrollment creation endpoint
    try {
      const response = await axios.post('http://localhost:5000/api/enrollments/create-after-payment', enrollmentData);
      
      if (response.data.success) {
        console.log('✅ Auto-enrollment created from checkout session:', response.data.data._id);
      } else {
        console.log('❌ Failed to create enrollment:', response.data.message);
      }
    } catch (enrollmentError) {
      console.log('❌ Error calling enrollment endpoint:', enrollmentError.message);
    }

  } catch (error) {
    console.error('❌ Error in handleSuccessfulCheckout:', error);
  }
}

// Handle payment that requires action (3D Secure, etc.)
async function handlePaymentRequiresAction(paymentIntent) {
  try {
    console.log('🔄 Processing payment that requires action...');
    console.log('🆔 Payment Intent ID:', paymentIntent.id);
    console.log('🔧 Status:', paymentIntent.status);
    console.log('⚡ Next action:', paymentIntent.next_action);
    console.log('📧 Customer email:', paymentIntent.metadata?.userEmail || 'N/A');
    console.log('🎯 Course ID:', paymentIntent.metadata?.courseId || 'N/A');
    
    // Log untuk debugging
    console.log('ℹ️ Payment requires additional authentication or verification');
    console.log('ℹ️ This payment will be processed when customer completes the required action');
    
  } catch (error) {
    console.error('❌ Error in handlePaymentRequiresAction:', error);
  }
}

// Handle payment that requires payment method
async function handlePaymentRequiresMethod(paymentIntent) {
  try {
    console.log('💳 Processing payment that requires payment method...');
    console.log('🆔 Payment Intent ID:', paymentIntent.id);
    console.log('🔧 Status:', paymentIntent.status);
    console.log('📧 Customer email:', paymentIntent.metadata?.userEmail || 'N/A');
    console.log('🎯 Course ID:', paymentIntent.metadata?.courseId || 'N/A');
    console.log('💰 Amount:', paymentIntent.amount, 'cents');
    
    // Log untuk debugging
    console.log('ℹ️ Payment Intent created but requires payment method attachment');
    console.log('ℹ️ Waiting for customer to provide payment details');
    
    // Optional: You can update payment status in database
    if (paymentIntent.metadata?.courseId && paymentIntent.metadata?.userEmail) {
      console.log('📝 Payment intent created for course:', paymentIntent.metadata.courseId);
      console.log('👤 Customer:', paymentIntent.metadata.userEmail);
    }
    
  } catch (error) {
    console.error('❌ Error in handlePaymentRequiresMethod:', error);
  }
}

// Stripe webhook endpoint
app.post('/api/webhooks/stripe', (req, res) => {
  const sig = req.headers['stripe-signature'];
  const endpointSecret = process.env.STRIPE_WEBHOOK_SECRET;

  let event;

  try {
    // Verify webhook signature
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    event = stripe.webhooks.constructEvent(req.body, sig, endpointSecret);
    
    console.log('✅ Stripe webhook received:', event.type);
    console.log('📦 Event data:', JSON.stringify(event.data.object, null, 2));
  } catch (err) {
    console.log('❌ Webhook signature verification failed:', err.message);
    return res.status(400).send(`Webhook Error: ${err.message}`);
  }

  // Handle the event
  switch (event.type) {
    case 'payment_intent.succeeded':
      const paymentIntent = event.data.object;
      console.log('💰 Payment Intent succeeded:', paymentIntent.id);
      console.log('📋 Payment Intent metadata:', paymentIntent.metadata);
      handleSuccessfulPayment(paymentIntent);
      break;
      
    case 'payment_intent.requires_action':
      const requiresActionPayment = event.data.object;
      console.log('⚠️ Payment requires action:', requiresActionPayment.id);
      console.log('🔧 Status:', requiresActionPayment.status);
      console.log('📋 Metadata:', requiresActionPayment.metadata);
      handlePaymentRequiresAction(requiresActionPayment);
      break;
      
    case 'payment_intent.created':
      const createdPayment = event.data.object;
      console.log('🆕 Payment Intent created:', createdPayment.id);
      console.log('🔧 Status:', createdPayment.status);
      console.log('📋 Metadata:', createdPayment.metadata);
      if (createdPayment.status === 'requires_payment_method') {
        handlePaymentRequiresMethod(createdPayment);
      }
      break;
      
    case 'payment_intent.payment_failed':
      const failedPayment = event.data.object;
      console.log('❌ Payment failed:', failedPayment.id);
      break;
      
    case 'checkout.session.completed':
      const session = event.data.object;
      console.log('🎉 Checkout Session completed:', session.id);
      console.log('📋 Session metadata:', session.metadata);
      handleSuccessfulCheckout(session);
      break;
      
    default:
      console.log(`🔔 Unhandled event type: ${event.type}`);
  }

  // Return a 200 response to acknowledge receipt of the event
  res.json({ received: true, event_type: event.type });
});

// Test endpoint untuk create payment intent
app.post('/api/payments/create-payment-intent', async (req, res) => {
  try {
    const { amount, currency = 'usd', courseId, userId } = req.body;
    
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amount * 100, // Convert to cents
      currency,
      metadata: {
        courseId: courseId || 'test-course',
        userId: userId || 'test-user',
        source: 'edemy_lms'
      }
    });

    console.log('💳 Payment Intent created:', paymentIntent.id);
    console.log('📋 Metadata:', paymentIntent.metadata);

    res.json({
      success: true,
      clientSecret: paymentIntent.client_secret,
      paymentIntentId: paymentIntent.id
    });
    
  } catch (error) {
    console.error('❌ Payment intent creation failed:', error);
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// Test endpoint untuk Stripe connection
app.get('/api/payments/test-stripe-connection', async (req, res) => {
  try {
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    
    // Test dengan retrieve account info
    const account = await stripe.accounts.retrieve();
    
    console.log('✅ Stripe connection test successful');
    console.log('🏢 Account ID:', account.id);
    
    res.json({
      success: true,
      message: 'Stripe connection successful',
      account: {
        id: account.id,
        business_profile: account.business_profile,
        country: account.country,
        default_currency: account.default_currency,
        details_submitted: account.details_submitted
      },
      test_mode: process.env.STRIPE_SECRET_KEY?.startsWith('sk_test_')
    });
    
  } catch (error) {
    console.error('❌ Stripe connection test failed:', error);
    res.status(500).json({
      success: false,
      error: error.message,
      suggestion: 'Check STRIPE_SECRET_KEY in .env file'
    });
  }
});

// Endpoint untuk simulate real payment completion (untuk testing)
app.post('/api/payments/simulate-real-payment', async (req, res) => {
  try {
    const { courseId, userEmail, amount = 50000 } = req.body;
    
    console.log('🧪 Simulating real payment completion...');
    console.log('📧 Customer email:', userEmail);
    console.log('🎯 Course ID:', courseId);
    console.log('💰 Amount:', amount, 'cents');
    
    // Find user by email
    const User = require('./models/User');
    const user = await User.findOne({ email: userEmail });
    
    if (!user) {
      return res.status(404).json({
        success: false,
        error: 'User not found with email: ' + userEmail,
        availableUsers: await User.find({}, 'email name clerkId').limit(3)
      });
    }
    
    // Check if course exists
    const Course = require('./models/Course');
    const course = await Course.findById(courseId);
    
    if (!course) {
      return res.status(404).json({
        success: false,
        error: 'Course not found with ID: ' + courseId,
        availableCourses: await Course.find({}, '_id title price').limit(3)
      });
    }
    
    // Simulate payment intent object
    const simulatedPaymentIntent = {
      id: 'pi_simulated_' + Date.now(),
      amount: amount,
      currency: 'usd',
      status: 'succeeded',
      metadata: {
        courseId: courseId,
        userEmail: userEmail,
        userId: user.clerkId
      }
    };
    
    // Call the actual webhook handler
    await handleSuccessfulPayment(simulatedPaymentIntent);
    
    res.json({
      success: true,
      message: 'Payment simulation completed successfully',
      paymentIntent: simulatedPaymentIntent,
      user: {
        id: user.clerkId,
        email: user.email,
        name: user.name
      },
      course: {
        id: course._id,
        title: course.title,
        price: course.price
      },
      note: 'This simulates what happens when a real Stripe payment completes'
    });
    
  } catch (error) {
    console.error('❌ Error simulating payment:', error);
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

app.listen(PORT, () => {
  console.log(`🚀 Server running on http://localhost:${PORT}`);
  console.log(`🎯 Stripe webhook endpoint: http://localhost:${PORT}/api/webhooks/stripe`);
  console.log(`💳 Payment endpoint: http://localhost:${PORT}/api/payments/create-payment-intent`);
  console.log(`🛒 Checkout endpoint: http://localhost:${PORT}/api/payments/create-checkout-session`);
  console.log(`✅ Verify endpoint: http://localhost:${PORT}/api/payments/verify-payment`);
  console.log(`📋 Test endpoint: http://localhost:${PORT}/api/payments/test-success`);
  console.log(`🔄 Waiting for Stripe webhooks...`);
});