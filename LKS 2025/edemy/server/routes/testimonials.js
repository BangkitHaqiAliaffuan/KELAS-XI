const express = require('express');
const Testimonial = require('../models/Testimonial');
const router = express.Router();

// GET /api/testimonials - Get all testimonials
router.get('/', async (req, res) => {
  try {
    const { page = 1, limit = 10, courseId } = req.query;
    
    const filter = { isActive: true };
    if (courseId) filter.course = courseId;

    const skip = (Number(page) - 1) * Number(limit);
    
    const testimonials = await Testimonial.find(filter)
      .populate('course', 'title thumbnail')
      .populate('user', 'name image')
      .skip(skip)
      .limit(Number(limit))
      .sort({ createdAt: -1 });

    const total = await Testimonial.countDocuments(filter);

    res.json({
      success: true,
      data: testimonials,
      pagination: {
        current: Number(page),
        pages: Math.ceil(total / Number(limit)),
        total
      }
    });

  } catch (error) {
    console.error('Error fetching testimonials:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching testimonials',
      error: error.message
    });
  }
});

// GET /api/testimonials/featured - Get featured testimonials for homepage
router.get('/featured', async (req, res) => {
  try {
    const testimonials = await Testimonial.find({ 
      isActive: true,
      rating: { $gte: 4 }
    })
    .populate('course', 'title')
    .sort({ rating: -1, createdAt: -1 })
    .limit(6);

    res.json({
      success: true,
      data: testimonials
    });

  } catch (error) {
    console.error('Error fetching featured testimonials:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching featured testimonials',
      error: error.message
    });
  }
});

// POST /api/testimonials - Create new testimonial
router.post('/', async (req, res) => {
  try {
    const testimonial = new Testimonial(req.body);
    await testimonial.save();

    await testimonial.populate('course', 'title thumbnail');
    await testimonial.populate('user', 'name image');

    res.status(201).json({
      success: true,
      data: testimonial,
      message: 'Testimonial created successfully'
    });

  } catch (error) {
    console.error('Error creating testimonial:', error);
    res.status(400).json({
      success: false,
      message: 'Error creating testimonial',
      error: error.message
    });
  }
});

module.exports = router;
