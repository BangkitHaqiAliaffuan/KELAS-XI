const mongoose = require('mongoose');

const courseSchema = new mongoose.Schema({
  courseTitle: {
    type: String,
    required: true,
    trim: true
  },
  courseDescription: {
    type: String,
    required: true
  },
  coursePrice: {
    type: Number,
    required: true,
    min: 0
  },
  courseThumbnail: {
    type: String,
    required: true
  },
  discount: {
    type: Number,
    default: 0,
    min: 0,
    max: 100
  },
  isPublished: {
    type: Boolean,
    default: true
  },
  educator: {
    type: String,
    required: true
  },
  courseContent: [{
    chapterId: String,
    chapterOrder: Number,
    chapterTitle: String,
    chapterContent: [{
      lectureId: String,
      lectureTitle: String,
      lectureDuration: Number,
      lectureUrl: String,
      isPreviewFree: {
        type: Boolean,
        default: false
      },
      lectureOrder: Number
    }]
  }],
  enrolledStudents: [{
    type: String // Clerk user IDs
  }],
  courseRatings: [{
    userId: String,
    rating: {
      type: Number,
      min: 1,
      max: 5
    },
    review: String,
    createdAt: {
      type: Date,
      default: Date.now
    }
  }],
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
}, {
  collection: 'courses' // Explicitly specify collection name
});

// Update the updatedAt field before saving
courseSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

module.exports = mongoose.model('Course', courseSchema);
