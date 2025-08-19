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
  } catch (err) {
    console.log('❌ Webhook signature verification failed:', err.message);
    return res.status(400).send(`Webhook Error: ${err.message}`);
  }

  // Handle the event
  switch (event.type) {
    case 'payment_intent.succeeded':
      const paymentIntent = event.data.object;
      console.log('💰 Payment succeeded:', paymentIntent.id);
      // TODO: Handle successful payment (enroll user to course)
      break;
      
    case 'payment_intent.payment_failed':
      const failedPayment = event.data.object;
      console.log('❌ Payment failed:', failedPayment.id);
      // TODO: Handle failed payment
      break;
      
    case 'checkout.session.completed':
      const session = event.data.object;
      console.log('🎉 Checkout completed:', session.id);
      // TODO: Handle successful checkout
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
    const { amount, currency = 'usd', courseId } = req.body;
    
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amount * 100, // Convert to cents
      currency,
      metadata: {
        courseId,
        source: 'edemy_lms'
      }
    });

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

// Courses endpoint (placeholder) - DEPRECATED: Use /api/courses instead
app.get('/api/courses-old', (req, res) => {
  res.json({
    message: 'This endpoint is deprecated. Use /api/courses instead',
    redirect: '/api/courses'
  });
});

app.listen(PORT, () => {
  console.log(`🚀 Server running on http://localhost:${PORT}`);
  console.log(`🎯 Stripe webhook endpoint: http://localhost:${PORT}/api/webhooks/stripe`);
  console.log(`💳 Payment endpoint: http://localhost:${PORT}/api/payments/create-payment-intent`);
});