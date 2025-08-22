const express = require('express');
const Enrollment = require('../models/Enrollment');
const Course = require('../models/Course');
const User = require('../models/User');
const router = express.Router();

// GET /api/student-dashboard/:clerkId - Get student dashboard data
router.get('/:clerkId', async (req, res) => {
  try {
    const { clerkId } = req.params;

    console.log('📊 Fetching dashboard data for clerkId:', clerkId);

    // Find user with better error handling
    let user;
    try {
      user = await User.findOne({ clerkId });
      console.log('👤 User found:', user ? 'Yes' : 'No');
    } catch (userError) {
      console.error('❌ Error finding user:', userError);
      return res.status(500).json({
        success: false,
        message: 'Error finding user',
        error: userError.message
      });
    }

    // If user not found, try to create a basic dashboard without user data
    if (!user) {
      console.log('⚠️ User not found, returning empty dashboard');
      return res.json({
        success: true,
        data: {
          user: {
            name: 'Guest User',
            email: 'guest@example.com',
            avatar: null,
            clerkId: clerkId
          },
          stats: {
            totalEnrolledCourses: 0,
            totalHoursLearned: 0,
            completedCourses: 0,
            averageProgress: 0
          },
          currentLearning: [],
          recommendedCourses: [],
          recentEnrollments: []
        }
      });
    }

    // Get user's enrollments with error handling
    let enrollments = [];
    try {
      enrollments = await Enrollment.find({
        'student.clerkId': clerkId
      }).sort({ purchaseDate: -1 });
      console.log('📚 Found enrollments:', enrollments.length);
    } catch (enrollmentError) {
      console.error('❌ Error finding enrollments:', enrollmentError);
      // Continue with empty enrollments rather than failing
    }

    // Get enrolled course IDs for fetching detailed course info
    const enrolledCourseIds = enrollments.map(e => e.courseId);
    
    // Get detailed course information with error handling
    let enrolledCourses = [];
    try {
      if (enrolledCourseIds.length > 0) {
        enrolledCourses = await Course.find({
          _id: { $in: enrolledCourseIds }
        });
        console.log('🎓 Found courses:', enrolledCourses.length);
      }
    } catch (courseError) {
      console.error('❌ Error finding courses:', courseError);
      // Continue without course data
    }

    // Calculate statistics with safe defaults
    const totalEnrolledCourses = enrollments.length;
    const totalHoursLearned = enrollments.reduce((total, enrollment) => {
      try {
        const course = enrolledCourses.find(c => c._id.toString() === enrollment.courseId.toString());
        if (course && course.courseContent && Array.isArray(course.courseContent)) {
          const courseDuration = course.courseContent.reduce((chapterTotal, chapter) => {
            if (chapter.chapterContent && Array.isArray(chapter.chapterContent)) {
              return chapterTotal + chapter.chapterContent.reduce((lectureTotal, lecture) => {
                return lectureTotal + (lecture.lectureDuration || 0);
              }, 0);
            }
            return chapterTotal;
          }, 0);
          
          // Calculate hours based on progress
          return total + (courseDuration * (enrollment.progress / 100) / 60); // Convert minutes to hours
        }
        return total;
      } catch (calcError) {
        console.error('❌ Error calculating hours for enrollment:', enrollment.courseId, calcError);
        return total;
      }
    }, 0);

    const completedCourses = enrollments.filter(e => e.progress >= 100).length;
    const averageProgress = enrollments.length > 0 
      ? enrollments.reduce((sum, e) => sum + (e.progress || 0), 0) / enrollments.length 
      : 0;

    // Get current learning (courses with progress < 100, sorted by recent activity)
    const currentLearning = enrollments
      .filter(e => e.progress < 100)
      .map(enrollment => {
        try {
          const course = enrolledCourses.find(c => c._id.toString() === enrollment.courseId.toString());
          
          // Find current chapter/lecture based on progress
          let currentChapter = null;
          let currentLecture = null;
          
          if (course && course.courseContent && Array.isArray(course.courseContent)) {
            const totalLectures = course.courseContent.reduce((total, chapter) => {
              return total + (chapter.chapterContent?.length || 0);
            }, 0);
            
            const completedLecturesCount = Math.floor((enrollment.progress / 100) * totalLectures);
            let lectureIndex = 0;
            
            for (const chapter of course.courseContent) {
              if (chapter.chapterContent && Array.isArray(chapter.chapterContent)) {
                for (const lecture of chapter.chapterContent) {
                  if (lectureIndex === completedLecturesCount) {
                    currentChapter = chapter;
                    currentLecture = lecture;
                    break;
                  }
                  lectureIndex++;
                }
                if (currentLecture) break;
              }
            }
          }
          
          return {
            _id: enrollment._id,
            courseId: enrollment.courseId,
            courseTitle: enrollment.courseTitle,
            progress: enrollment.progress || 0,
            currentChapter: currentChapter?.chapterTitle || 'Getting Started',
            currentLecture: currentLecture?.lectureTitle || 'Introduction',
            courseImage: course?.courseThumbnail || course?.courseImage || '/api/placeholder/150/100',
            lastAccessed: enrollment.updatedAt || enrollment.purchaseDate
          };
        } catch (mappingError) {
          console.error('❌ Error mapping enrollment:', enrollment.courseId, mappingError);
          return {
            _id: enrollment._id,
            courseId: enrollment.courseId,
            courseTitle: enrollment.courseTitle || 'Unknown Course',
            progress: enrollment.progress || 0,
            currentChapter: 'Getting Started',
            currentLecture: 'Introduction',
            courseImage: '/api/placeholder/150/100',
            lastAccessed: enrollment.purchaseDate
          };
        }
      })
      .slice(0, 3); // Get top 3 current learning courses

    // Get recommended courses (courses not enrolled in) with error handling
    let recommendedCourses = [];
    try {
      const allCourses = await Course.find({
        isPublished: true,
        _id: { $nin: enrolledCourseIds }
      }).limit(6);

      recommendedCourses = allCourses.map(course => {
        try {
          return {
            _id: course._id,
            title: course.courseTitle || course.title || 'Untitled Course',
            description: course.courseDescription?.replace(/<[^>]*>/g, '').substring(0, 100) + '...' || 'No description available',
            price: course.coursePrice || course.price || 0,
            originalPrice: course.discount ? (course.coursePrice / (1 - course.discount / 100)).toFixed(2) : null,
            discount: course.discount || 0,
            image: course.courseThumbnail || course.courseImage || '/api/placeholder/300/200',
            instructor: course.instructor || 'Expert Instructor',
            rating: course.rating || 4.5,
            studentsCount: course.enrolledStudents?.length || 0
          };
        } catch (mappingError) {
          console.error('❌ Error mapping course:', course._id, mappingError);
          return {
            _id: course._id,
            title: 'Course',
            description: 'Course description',
            price: 0,
            originalPrice: null,
            discount: 0,
            image: '/api/placeholder/300/200',
            instructor: 'Instructor',
            rating: 4.5,
            studentsCount: 0
          };
        }
      });
    } catch (recommendedError) {
      console.error('❌ Error fetching recommended courses:', recommendedError);
      recommendedCourses = [];
    }

    const dashboardData = {
      user: {
        name: user.name || 'User',
        email: user.email || 'user@example.com',
        avatar: user.imageUrl || null,
        clerkId: user.clerkId
      },
      stats: {
        totalEnrolledCourses,
        totalHoursLearned: Math.round(totalHoursLearned * 10) / 10, // Round to 1 decimal
        completedCourses,
        averageProgress: Math.round(averageProgress)
      },
      currentLearning,
      recommendedCourses,
      recentEnrollments: enrollments.slice(0, 5).map(e => ({
        _id: e._id,
        courseTitle: e.courseTitle || 'Unknown Course',
        purchaseDate: e.purchaseDate,
        progress: e.progress || 0
      }))
    };

    console.log('✅ Dashboard data prepared successfully');

    res.json({
      success: true,
      data: dashboardData
    });

  } catch (error) {
    console.error('❌ Error fetching student dashboard data:', error);
    console.error('❌ Error stack:', error.stack);
    
    // Return a safe error response
    res.status(500).json({
      success: false,
      message: 'Error fetching dashboard data',
      error: process.env.NODE_ENV === 'development' ? error.message : 'Internal server error'
    });
  }
});

// GET /api/student-dashboard/progress/:clerkId - Get detailed progress for all courses
router.get('/progress/:clerkId', async (req, res) => {
  try {
    const { clerkId } = req.params;

    const enrollments = await Enrollment.find({
      'student.clerkId': clerkId
    });

    const progressData = enrollments.map(enrollment => ({
      courseId: enrollment.courseId,
      courseTitle: enrollment.courseTitle,
      progress: enrollment.progress,
      completedLectures: enrollment.completedLectures,
      totalLectures: enrollment.totalLectures,
      purchaseDate: enrollment.purchaseDate,
      lastAccessed: enrollment.updatedAt || enrollment.purchaseDate
    }));

    res.json({
      success: true,
      data: progressData
    });

  } catch (error) {
    console.error('❌ Error fetching progress data:', error);
    res.status(500).json({
      success: false,
      message: 'Error fetching progress data',
      error: error.message
    });
  }
});

module.exports = router;
