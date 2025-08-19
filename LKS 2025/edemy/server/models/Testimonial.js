const mongoose = require('mongoose');

const testimonialSchema = new mongoose.Schema({
  userName: {
    type: String,
    required: true,
    trim: true
  },
  userDesignation: {
    type: String,
    required: true,
    trim: true
  },
  userImage: {
    type: String,
    required: true
  },
  testimonialText: {
    type: String,
    required: true,
    trim: true
  },
  rating: {
    type: Number,
    required: true,
    min: 1,
    max: 5
  },
  courseName: {
    type: String,
    required: true,
    trim: true
  },
  isActive: {
    type: Boolean,
    default: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
}, {
  collection: 'testimonials' // Explicitly specify collection name
});

// Update the updatedAt field before saving
testimonialSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

module.exports = mongoose.model('Testimonial', testimonialSchema);
