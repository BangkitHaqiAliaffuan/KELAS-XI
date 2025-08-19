const express = require('express');
const Enrollment = require('../models/Enrollment');
const Course = require('../models/Course');
const router = express.Router();

// GET /api/enrollments - Get all enrollments (admin) or user's enrollments
router.get('/', async (req, res) => {
  try {
    const { page = 1, limit = 10, userId, status } = req.query;
    
    const filter = {};
    if (userId) filter.user = userId;
    if (status) filter.paymentStatus = status;

    const skip = (Number(page) - 1) * Number(limit);
    
    const enrollments = await Enrollment.find(filter)
      .populate('course', 'title thumbnail price instructor duration lessons')
      .skip(skip)
      .limit(Number(limit))
      .sort({ enrollmentDate: -1 });

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
      user: userId,
      paymentStatus: 'completed'
    })
    .populate('course', 'title thumbnail price instructor duration lessons rating')
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
    const { user, courseId, paymentIntentId, amount } = req.body;

    // Check if user is already enrolled
    const existingEnrollment = await Enrollment.findOne({
      user,
      course: courseId
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
      user,
      course: courseId,
      paymentIntentId,
      amount,
      paymentStatus: 'pending'
    });

    await enrollment.save();
    await enrollment.populate('course', 'title thumbnail price instructor');

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
