const mongoose = require('mongoose');

const enrollmentSchema = new mongoose.Schema({
  userId: {
    type: String, // Clerk user ID
    required: true
  },
  courseId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Course',
    required: true
  },
  enrollmentDate: {
    type: Date,
    default: Date.now
  },
  progress: {
    type: Number,
    default: 0,
    min: 0,
    max: 100
  },
  completedLessons: [{
    lessonId: String,
    completedAt: {
      type: Date,
      default: Date.now
    }
  }],
  isCompleted: {
    type: Boolean,
    default: false
  },
  paymentStatus: {
    type: String,
    enum: ['pending', 'completed', 'failed', 'refunded'],
    default: 'pending'
  },
  paymentIntentId: {
    type: String
  },
  amount: {
    type: Number,
    required: true
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
  collection: 'enrollments' // Explicitly specify collection name
});

// Update the updatedAt field before saving
enrollmentSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Create compound index for user and course
enrollmentSchema.index({ userId: 1, courseId: 1 }, { unique: true });

module.exports = mongoose.model('Enrollment', enrollmentSchema);
