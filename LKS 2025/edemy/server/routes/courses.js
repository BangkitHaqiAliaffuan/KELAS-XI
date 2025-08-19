const express = require('express');
const Course = require('../models/Course');
const router = express.Router();

// DEBUG route - untuk melihat data raw di database
router.get('/debug', async (req, res) => {
  try {
    const mongoose = require('mongoose');
    const db = mongoose.connection.db;
    
    // Cek semua collections
    const collections = await db.listCollections().toArray();
    console.log('Available collections:', collections.map(c => c.name));
    
    // Cek data di collection courses
    const coursesCollection = db.collection('courses');
    const rawCourses = await coursesCollection.find({}).toArray();
    console.log('Raw courses count:', rawCourses.length);
    console.log('Sample raw course:', rawCourses[0]);
    
    // Cek dengan Mongoose model
    const modelCourses = await Course.find({});
    console.log('Model courses count:', modelCourses.length);
    console.log('Sample model course:', modelCourses[0]);
    
    res.json({
      collections: collections.map(c => c.name),
      rawCoursesCount: rawCourses.length,
      modelCoursesCount: modelCourses.length,
      sampleRawCourse: rawCourses[0],
      sampleModelCourse: modelCourses[0]
    });
  } catch (error) {
    console.error('Debug error:', error);
    res.status(500).json({ error: error.message });
  }
});

// GET /api/courses - Get all courses with pagination and filters
router.get('/', async (req, res) => {
  try {
    const { 
      page = 1, 
      limit = 12, 
      category, 
      level, 
      minPrice, 
      maxPrice, 
      search,
      sortBy = 'createdAt',
      sortOrder = 'desc'
    } = req.query;

    // Build filter object - start with empty filter
    const filter = {};
    
    // Log untuk debugging
    console.log('Query params:', req.query);
    
    if (category) {
      filter.category = category;
    }
    
    if (level) {
      filter.level = level;
    }
    
    if (search) {
      filter.$or = [
        { courseTitle: { $regex: search, $options: 'i' } },
        { courseDescription: { $regex: search, $options: 'i' } },
        { educator: { $regex: search, $options: 'i' } }
      ];
    }
    
    if (minPrice || maxPrice) {
      filter.coursePrice = {};
      if (minPrice) filter.coursePrice.$gte = Number(minPrice);
      if (maxPrice) filter.coursePrice.$lte = Number(maxPrice);
    }

    console.log('Applied filter:', JSON.stringify(filter, null, 2));

    // Calculate pagination
    const skip = (Number(page) - 1) * Number(limit);
    
    // Sort object
    const sort = {};
    sort[sortBy] = sortOrder === 'desc' ? -1 : 1;

    // Execute query
    const courses = await Course.find(filter)
      .sort(sort)
      .skip(skip)
      .limit(Number(limit));

    console.log('Found courses:', courses.length);

    // Get total count for pagination
    const total = await Course.countDocuments(filter);
    
    console.log('Total count:', total);
    
    res.json({
      success: true,
      data: courses,
      pagination: {
        current: Number(page),
        pages: Math.ceil(total / Number(limit)),
        total,
        hasNext: skip + courses.length < total,
        hasPrev: Number(page) > 1
      }
    });

  } catch (error) {
    console.error('Error fetching courses:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching courses',
      error: error.message
    });
  }
});

// GET /api/courses/featured - Get featured courses
router.get('/featured', async (req, res) => {
  try {
    const courses = await Course.find({ 
      isPublished: true
    })
    .sort({ enrolledStudents: -1, createdAt: -1 })
    .limit(6);

    res.json({
      success: true,
      data: courses
    });

  } catch (error) {
    console.error('Error fetching featured courses:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching featured courses',
      error: error.message
    });
  }
});

// GET /api/courses/categories - Get all course categories
router.get('/categories', async (req, res) => {
  try {
    const categories = await Course.distinct('educator', { isPublished: true });
    
    res.json({
      success: true,
      data: categories
    });

  } catch (error) {
    console.error('Error fetching categories:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching categories',
      error: error.message
    });
  }
});

// GET /api/courses/:id - Get single course by ID
router.get('/:id', async (req, res) => {
  try {
    const course = await Course.findById(req.params.id);
    
    if (!course || !course.isPublished) {
      return res.status(404).json({
        success: false,
        message: 'Course not found'
      });
    }

    res.json({
      success: true,
      data: course
    });

  } catch (error) {
    console.error('Error fetching course:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching course',
      error: error.message
    });
  }
});

// POST /api/courses - Create new course (for educators)
router.post('/', async (req, res) => {
  try {
    const course = new Course(req.body);
    await course.save();

    res.status(201).json({
      success: true,
      data: course,
      message: 'Course created successfully'
    });

  } catch (error) {
    console.error('Error creating course:', error);
    res.status(400).json({
      success: false,
      message: 'Error creating course',
      error: error.message
    });
  }
});

module.exports = router;
