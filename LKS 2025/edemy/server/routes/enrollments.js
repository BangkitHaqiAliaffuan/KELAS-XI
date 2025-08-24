const express = require('express');
const Enrollment = require('../models/Enrollment');
const Course = require('../models/Course');
const User = require('../models/User');
const router = express.Router();

// POST /api/enrollments/create-after-payment - Create enrollment after successful payment
router.post('/create-after-payment', async (req, res) => {
  try {
    const { 
      clerkId, 
      courseId, 
      paymentIntentId, 
      sessionId,
      amount 
    } = req.body;

    console.log('Creating enrollment after payment:', { clerkId, courseId, paymentIntentId });

    // Find user by clerkId
    const user = await User.findOne({ clerkId });
    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found. Please sync user first.',
        error: 'USER_NOT_FOUND'
      });
    }

    // Find course
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    // Check if enrollment already exists
    const existingEnrollment = await Enrollment.findOne({
      'student.clerkId': clerkId,
      courseId: courseId
    });

    if (existingEnrollment) {
      return res.json({
        success: true,
        message: 'User already enrolled in this course',
        data: existingEnrollment
      });
    }

    // Create new enrollment
    const enrollment = new Enrollment({
      student: {
        clerkId: user.clerkId,
        name: user.name,
        imageUrl: user.imageUrl || 'https://img.clerk.com/eyJ0eXBlIjoicHJveHkiLCJzcmMiOiJodHRwczovL2ltYWdlcy5jbGVyay5kZXYvb2F1dGhfZ29vZ2xlL2ltZ18ycVFsdmFMSkw3ckIxNHZMU2o4ZURWNEtmR2IifQ'
      },
      courseId: course._id,
      courseTitle: course.title,
      purchaseDate: new Date(),
      progress: 0,
      completedLectures: 0,
      totalLectures: course.lessons ? course.lessons.length : 4,
      paymentIntentId,
      sessionId,
      amount
    });

    await enrollment.save();

    console.log('Enrollment created successfully:', enrollment._id);

    res.json({
      success: true,
      message: 'Enrollment created successfully',
      data: enrollment
    });

  } catch (error) {
    console.error('Error creating enrollment:', error);
    res.status(500).json({
      success: false,
      message: 'Error creating enrollment',
      error: error.message
    });
  }
});

// GET /api/enrollments - Get all enrollments (admin) or user's enrollments
router.get('/', async (req, res) => {
  try {
    const { 
      page = 1, 
      limit = 10, 
      userId, 
      instructorId, 
      status,
      sortBy = 'enrollmentDate',
      sortOrder = 'desc'
    } = req.query;
    
    const filter = {};
    if (userId) filter.user = userId;
    if (status) filter.paymentStatus = status;

    // If instructorId is provided, find enrollments for courses taught by this instructor
    if (instructorId) {
      const instructorCourses = await Course.find({ instructorId }).select('_id');
      const courseIds = instructorCourses.map(course => course._id);
      filter.courseId = { $in: courseIds };
    }

    const skip = (Number(page) - 1) * Number(limit);
    
    // Build sort object
    const sort = {};
    sort[sortBy] = sortOrder === 'asc' ? 1 : -1;
    
    const enrollments = await Enrollment.find(filter)
      .populate('course', 'courseTitle courseThumbnail coursePrice instructor')
      .populate('student', 'name email profileImage')
      .skip(skip)
      .limit(Number(limit))
      .sort(sort);

    const total = await Enrollment.countDocuments(filter);

    res.json({
      success: true,
      data: enrollments,
      pagination: {
        current: Number(page),
        pages: Math.ceil(total / Number(limit)),
        total
      }
    });

  } catch (error) {
    console.error('Error fetching enrollments:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching enrollments',
      error: error.message
    });
  }
});

// GET /api/enrollments/user/:userId - Get user's enrolled courses
router.get('/user/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    
    const enrollments = await Enrollment.find({ 
      userId: userId,
      paymentStatus: 'completed'
    })
    .populate('courseId', 'courseTitle courseThumbnail coursePrice educator duration lessons rating')
    .sort({ enrollmentDate: -1 });

    res.json({
      success: true,
      data: enrollments
    });

  } catch (error) {
    console.error('Error fetching user enrollments:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching user enrollments',
      error: error.message
    });
  }
});

// POST /api/enrollments - Create new enrollment
router.post('/', async (req, res) => {
  try {
    console.log('Received enrollment request body:', req.body);
    const { userId, courseId, paymentIntentId, amount } = req.body;
    console.log('Extracted values:', { userId, courseId, paymentIntentId, amount });

    // Validate required fields
    if (!userId || !courseId || !amount) {
      console.log('Validation failed - missing fields:', { userId: !!userId, courseId: !!courseId, amount: !!amount });
      return res.status(400).json({
        success: false,
        message: 'Missing required fields: userId, courseId, and amount are required'
      });
    }

    // Check if user is already enrolled
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

    // Check if course exists
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    const enrollment = new Enrollment({
      userId,
      courseId,
      paymentIntentId,
      amount,
      paymentStatus: 'pending'
    });

    await enrollment.save();
    await enrollment.populate('courseId', 'courseTitle courseThumbnail coursePrice educator');

    res.status(201).json({
      success: true,
      data: enrollment,
      message: 'Enrollment created successfully'
    });

  } catch (error) {
    console.error('Error creating enrollment:', error);
    res.status(400).json({
      success: false,
      message: 'Error creating enrollment',
      error: error.message
    });
  }
});

// PUT /api/enrollments/:id/status - Update enrollment status (for payment confirmation)
router.put('/:id/status', async (req, res) => {
  try {
    const { status } = req.body;
    
    const enrollment = await Enrollment.findByIdAndUpdate(
      req.params.id,
      { paymentStatus: status },
      { new: true }
    ).populate('course', 'title thumbnail');

    if (!enrollment) {
      return res.status(404).json({
        success: false,
        message: 'Enrollment not found'
      });
    }

    // If payment is completed, increment course enrollment count
    if (status === 'completed') {
      await Course.findByIdAndUpdate(
        enrollment.course._id,
        { $inc: { studentsEnrolled: 1 } }
      );
    }

    res.json({
      success: true,
      data: enrollment,
      message: 'Enrollment status updated successfully'
    });

  } catch (error) {
    console.error('Error updating enrollment status:', error);
    res.status(400).json({
      success: false,
      message: 'Error updating enrollment status',
      error: error.message
    });
  }
});

// PUT /api/enrollments/:id/progress - Update course progress
router.put('/:id/progress', async (req, res) => {
  try {
    const { progress, lessonId } = req.body;
    
    const updateData = { progress };
    
    // If lesson is completed, add to completedLessons array
    if (lessonId) {
      updateData.$addToSet = {
        completedLessons: {
          lessonId,
          completedAt: new Date()
        }
      };
    }

    // Mark as completed if progress is 100%
    if (progress >= 100) {
      updateData.isCompleted = true;
    }

    const enrollment = await Enrollment.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true }
    ).populate('course', 'title thumbnail');

    if (!enrollment) {
      return res.status(404).json({
        success: false,
        message: 'Enrollment not found'
      });
    }

    res.json({
      success: true,
      data: enrollment,
      message: 'Progress updated successfully'
    });

  } catch (error) {
    console.error('Error updating progress:', error);
    res.status(400).json({
      success: false,
      message: 'Error updating progress',
      error: error.message
    });
  }
});

module.exports = router;
