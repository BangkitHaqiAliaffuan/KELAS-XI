const express = require('express');
const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
const Course = require('../models/Course');
const Enrollment = require('../models/Enrollment');
const User = require('../models/User');
const router = express.Router();

// POST /api/payments/create-checkout-session - Create Stripe checkout session
router.post('/create-checkout-session', async (req, res) => {
  try {
    const { courseId, userId, successUrl, cancelUrl } = req.body;

    console.log('Creating checkout session for:', { courseId, userId });

    // Get course details
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Get user details
    const user = await User.findOne({ clerkId: userId });
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Check if user already enrolled
    const existingEnrollment = await Enrollment.findOne({
      'student.clerkId': userId,
      courseId: courseId
    });

    if (existingEnrollment) {
      return res.status(400).json({
        success: false,
        message: 'You are already enrolled in this course'
      });
    }

    // Calculate discount if any
    let price = course.coursePrice;
    let discountPercentage = 0;
    
    if (course.courseDiscount > 0) {
      discountPercentage = course.courseDiscount;
      price = course.coursePrice * (1 - discountPercentage / 100);
    }

    // Create Stripe checkout session
    const session = await stripe.checkout.sessions.create({
      payment_method_types: ['card'],
      line_items: [
        {
          price_data: {
            currency: 'usd',
            product_data: {
              name: course.courseTitle,
              description: course.courseDescription?.replace(/<[^>]*>/g, '').substring(0, 200) + '...',
              images: course.courseThumbnail ? [course.courseThumbnail] : [],
            },
            unit_amount: Math.round(price * 100), // Convert to cents
          },
          quantity: 1,
        },
      ],
      mode: 'payment',
      success_url: successUrl || `${process.env.CLIENT_URL}/course/${courseId}?payment=success`,
      cancel_url: cancelUrl || `${process.env.CLIENT_URL}/course/${courseId}?payment=cancelled`,
      metadata: {
        courseId: courseId,
        userId: userId,
        courseName: course.courseTitle,
        originalPrice: course.coursePrice.toString(),
        discountApplied: discountPercentage.toString(),
        finalPrice: price.toString()
      },
      customer_email: user.email,
    });

    console.log('✅ Checkout session created:', session.id);

    res.json({
      success: true,
      data: {
        sessionId: session.id,
        url: session.url,
        courseDetails: {
          title: course.courseTitle,
          price: course.coursePrice,
          discountedPrice: price,
          discountPercentage: discountPercentage
        }
      }
    });

  } catch (error) {
    console.error('Error creating checkout session:', error);
    res.status(500).json({
      success: false,
      message: 'Error creating checkout session',
      error: error.message
    });
  }
});

// POST /api/payments/create-payment-intent - Create payment intent for custom checkout
router.post('/create-payment-intent', async (req, res) => {
  try {
    const { courseId, userId } = req.body;

    // Get course details
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Calculate final price
    let price = course.coursePrice;
    if (course.courseDiscount > 0) {
      price = course.coursePrice * (1 - course.courseDiscount / 100);
    }

    // Create payment intent
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(price * 100), // Convert to cents
      currency: 'usd',
      metadata: {
        courseId: courseId,
        userId: userId,
        courseName: course.courseTitle
      },
    });

    res.json({
      success: true,
      data: {
        clientSecret: paymentIntent.client_secret,
        paymentIntentId: paymentIntent.id,
        amount: price
      }
    });

  } catch (error) {
    console.error('Error creating payment intent:', error);
    res.status(500).json({
      success: false,
      message: 'Error creating payment intent',
      error: error.message
    });
  }
});

// GET /api/payments/session/:sessionId - Get checkout session details
router.get('/session/:sessionId', async (req, res) => {
  try {
    const session = await stripe.checkout.sessions.retrieve(req.params.sessionId);

    res.json({
      success: true,
      data: session
    });

  } catch (error) {
    console.error('Error retrieving session:', error);
    res.status(500).json({
      success: false,
      message: 'Error retrieving session',
      error: error.message
    });
  }
});

// POST /api/payments/verify-payment - Verify payment and create enrollment
router.post('/verify-payment', async (req, res) => {
  try {
    const { sessionId, paymentIntentId } = req.body;

    let paymentDetails = null;

    if (sessionId) {
      // Get session details from Stripe
      const session = await stripe.checkout.sessions.retrieve(sessionId);
      paymentDetails = {
        id: session.payment_intent,
        amount: session.amount_total / 100,
        courseId: session.metadata.courseId,
        userId: session.metadata.userId,
        status: session.payment_status
      };
    } else if (paymentIntentId) {
      // Get payment intent details from Stripe
      const paymentIntent = await stripe.paymentIntents.retrieve(paymentIntentId);
      paymentDetails = {
        id: paymentIntent.id,
        amount: paymentIntent.amount / 100,
        courseId: paymentIntent.metadata.courseId,
        userId: paymentIntent.metadata.userId,
        status: paymentIntent.status
      };
    }

    if (!paymentDetails) {
      return res.status(400).json({
        success: false,
        message: 'Invalid payment verification request'
      });
    }

    if (paymentDetails.status !== 'paid' && paymentDetails.status !== 'succeeded') {
      return res.status(400).json({
        success: false,
        message: 'Payment not completed'
      });
    }

    // Get course and user details
    const [course, user] = await Promise.all([
      Course.findById(paymentDetails.courseId),
      User.findOne({ clerkId: paymentDetails.userId })
    ]);

    if (!course || !user) {
      return res.status(404).json({
        success: false,
        message: 'Course or user not found'
      });
    }

    // Check if enrollment already exists
    const existingEnrollment = await Enrollment.findOne({
      'student.clerkId': paymentDetails.userId,
      courseId: paymentDetails.courseId
    });

    if (existingEnrollment) {
      return res.json({
        success: true,
        message: 'Already enrolled',
        data: existingEnrollment
      });
    }

    // Calculate course statistics
    let totalLectures = 0;
    if (course.courseContent && Array.isArray(course.courseContent)) {
      course.courseContent.forEach(chapter => {
        if (chapter.chapterContent && Array.isArray(chapter.chapterContent)) {
          totalLectures += chapter.chapterContent.length;
        }
      });
    }

    // Create enrollment
    const enrollment = new Enrollment({
      student: {
        clerkId: user.clerkId,
        name: user.name,
        imageUrl: user.image
      },
      courseId: course._id,
      courseTitle: course.courseTitle,
      purchaseDate: new Date(),
      paymentIntentId: paymentDetails.id,
      paymentStatus: 'completed',
      amount: paymentDetails.amount,
      progress: 0,
      completedLectures: 0,
      totalLectures: totalLectures
    });

    await enrollment.save();

    // Update course enrolled students count
    await Course.findByIdAndUpdate(
      course._id,
      { 
        $addToSet: { enrolledStudents: user.clerkId },
        $inc: { studentsEnrolled: 1 }
      }
    );

    console.log('✅ Enrollment created successfully:', enrollment._id);

    res.json({
      success: true,
      message: 'Payment verified and enrollment created successfully',
      data: enrollment
    });

  } catch (error) {
    console.error('Error verifying payment:', error);
    res.status(500).json({
      success: false,
      message: 'Error verifying payment',
      error: error.message
    });
  }
});

// POST /api/payments/test-success - Test successful payment flow (development only)
router.post('/test-success', async (req, res) => {
  try {
    const { courseId, userId } = req.body;

    if (!courseId || !userId) {
      return res.status(400).json({
        success: false,
        message: 'courseId and userId are required'
      });
    }

    // Get course details
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Check if user already enrolled
    const existingEnrollment = await Enrollment.findOne({
      userId,
      courseId
    });

    if (existingEnrollment) {
      return res.status(400).json({
        success: false,
        message: 'User is already enrolled in this course'
      });
    }

    // Calculate final price (with discount if applicable)
    let finalPrice = course.coursePrice;
    if (course.courseDiscount > 0) {
      finalPrice = course.coursePrice * (1 - course.courseDiscount / 100);
    }

    // Create enrollment directly (simulating successful payment)
    const enrollment = new Enrollment({
      userId,
      courseId,
      amount: finalPrice,
      paymentStatus: 'completed',
      paymentIntentId: `pi_test_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      progress: 0,
      isCompleted: false
    });

    await enrollment.save();
    await enrollment.populate('courseId', 'courseTitle courseThumbnail coursePrice educator');

    res.json({
      success: true,
      data: {
        enrollment,
        courseName: course.courseTitle,
        amountPaid: finalPrice,
        enrolledDate: enrollment.enrollmentDate,
        message: 'Test enrollment created successfully'
      },
      message: 'Payment simulation successful - enrollment created'
    });

  } catch (error) {
    console.error('Error in test payment:', error);
    res.status(500).json({
      success: false,
      message: 'Error simulating payment',
      error: error.message
    });
  }
});

module.exports = router;
