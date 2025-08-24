import React, { useState, useEffect } from 'react';
import { useParams, Link, useSearchParams } from 'react-router-dom';
import YouTube from 'react-youtube';
import { 
  Clock, 
  Users, 
  BookOpen, 
  Star, 
  Play, 
  Check, 
  ArrowLeft,
  Globe,
  Award,
  Target,
  AlertCircle
} from 'lucide-react';
import { courseAPI, userAPI } from '../services/api.js';
import { assets } from '../assets/assets.js';
import { getImageProps } from '../utils/imageUtils.js';
import PaymentButton from '../components/PaymentButton.jsx';
import EnrollmentStatus from '../components/EnrollmentStatus.jsx';

const CourseDetail = () => {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedSection, setSelectedSection] = useState('overview');
  const [showVideo, setShowVideo] = useState(false);
  const [usersData, setUsersData] = useState({}); // Cache untuk data user

  // Get proper image props for course thumbnail
  const imageProps = course ? getImageProps(course.courseThumbnail, course.courseTitle, 0) : null;
  const [educatorData, setEducatorData] = useState(null);
  const [paymentStatus, setPaymentStatus] = useState(null);

  // Check if debug mode is enabled (development mode or via URL parameter)
  const isDebugMode = process.env.NODE_ENV === 'development' || 
                     searchParams.get('debug') === 'true' ||
                     localStorage.getItem('paymentDebugMode') === 'true';

  // Extract YouTube video ID from URL
  const extractYouTubeId = (url) => {
    if (!url) return null;
    
    const patterns = [
      /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/|youtube\.com\/v\/)([^&\n?#]+)/,
      /^([a-zA-Z0-9_-]{11})$/ // Direct video ID
    ];
    
    for (const pattern of patterns) {
      const match = url.match(pattern);
      if (match) return match[1];
    }
    
    return null;
  };

  // Get the first available video URL from course content
  const getFirstVideoUrl = () => {
    if (!course?.courseContent || !Array.isArray(course.courseContent)) return null;
    
    for (const chapter of course.courseContent) {
      if (chapter.chapterContent && Array.isArray(chapter.chapterContent)) {
        for (const lecture of chapter.chapterContent) {
          if (lecture.lectureUrl) {
            return lecture.lectureUrl;
          }
        }
      }
    }
    
    return null;
  };

  const firstVideoUrl = getFirstVideoUrl();
  const videoId = extractYouTubeId(firstVideoUrl);

  useEffect(() => {
    fetchCourse();
    
    // Check for payment status in URL
    const paymentParam = searchParams.get('payment');
    if (paymentParam === 'success') {
      setPaymentStatus('success');
    } else if (paymentParam === 'cancelled') {
      setPaymentStatus('cancelled');
    }
  }, [id, searchParams]);

  const fetchCourse = async () => {
    try {
      setLoading(true);
      const response = await courseAPI.getCourse(id);
      console.log('Course data loaded:', response.data);
      console.log('Course thumbnail:', response.data?.courseThumbnail);
      setCourse(response.data);
      
      // Fetch user data for educator and reviewers
      await fetchUsersData(response.data);
    } catch (err) {
      console.error('Error fetching course:', err);
      setError('Failed to load course details. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  // Function to fetch user data for educator and reviewers
  const fetchUsersData = async (courseData) => {
    const userIds = new Set();
    
    // Add educator ID
    if (courseData.educator) {
      userIds.add(courseData.educator);
    }
    
    // Add reviewer IDs
    if (courseData.courseRatings && Array.isArray(courseData.courseRatings)) {
      courseData.courseRatings.forEach(review => {
        if (review.userId) {
          userIds.add(review.userId);
        }
      });
    }
    
    // Fetch all user data
    const userData = {};
    for (const userId of userIds) {
      try {
        const userResponse = await userAPI.getUser(userId);
        if (userResponse.success && userResponse.data) {
          userData[userId] = userResponse.data;
        }
      } catch (error) {
        console.error(`Error fetching user data for ${userId}:`, error);
        // Set fallback data
        userData[userId] = {
          name: `User ${userId.slice(0, 8)}`,
          email: null
        };
      }
    }
    
    setUsersData(userData);
    
    // Set educator data specifically
    if (courseData.educator && userData[courseData.educator]) {
      setEducatorData(userData[courseData.educator]);
    }
  };

  // Helper function to get user display name
  const getUserDisplayName = (userId) => {
    if (!userId) return 'Anonymous User';
    
    const userData = usersData[userId];
    if (userData) {
      return userData.name || userData.firstName || userData.email?.split('@')[0] || `User ${userId.slice(0, 8)}`;
    }
    
    return `User ${userId.slice(0, 8)}`;
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  // Calculate course statistics
  const calculateCourseStats = () => {
    let totalLectures = 0;
    let totalDurationMinutes = 0;
    
    if (course?.courseContent && Array.isArray(course.courseContent)) {
      course.courseContent.forEach(chapter => {
        if (chapter.chapterContent && Array.isArray(chapter.chapterContent)) {
          totalLectures += chapter.chapterContent.length;
          chapter.chapterContent.forEach(lecture => {
            if (lecture.duration) {
              totalDurationMinutes += lecture.duration;
            } else {
              totalDurationMinutes += 10; // Default 10 minutes
            }
          });
        }
      });
    }
    
    const hours = Math.floor(totalDurationMinutes / 60);
    const minutes = totalDurationMinutes % 60;
    let formattedDuration = '';
    
    if (hours > 0) {
      formattedDuration = `${hours}h`;
      if (minutes > 0) {
        formattedDuration += ` ${minutes}m`;
      }
    } else {
      formattedDuration = `${minutes}m`;
    }
    
    return {
      totalLectures,
      totalDuration: formattedDuration,
      enrolledCount: course?.enrolledStudents ? course.enrolledStudents.length : 0
    };
  };

  const { totalLectures, totalDuration, enrolledCount } = course ? calculateCourseStats() : { totalLectures: 0, totalDuration: '0m', enrolledCount: 0 };

  // Calculate average rating
  const calculateAverageRating = () => {
    if (!course?.courseRatings || course.courseRatings.length === 0) {
      return 0;
    }
    const sum = course.courseRatings.reduce((acc, rating) => acc + rating.rating, 0);
    return Math.round((sum / course.courseRatings.length) * 10) / 10;
  };

  const averageRating = course ? calculateAverageRating() : 0;

  const discountPercentage = course?.discount 
    ? course.discount
    : (course?.originalPrice && course?.originalPrice > course?.coursePrice 
        ? Math.round(((course.originalPrice - course.coursePrice) / course.originalPrice) * 100)
        : 0);

  const discountedPrice = course?.discount > 0 
    ? course.coursePrice * (1 - course.discount / 100)
    : course?.coursePrice;

  // Payment handlers
  const handlePaymentSuccess = (data) => {
    console.log('Payment successful:', data);
    setPaymentStatus('success');
    // Refresh course data to update enrollment status
    fetchCourse();
  };

  const handlePaymentError = (error) => {
    console.error('Payment error:', error);
    setPaymentStatus('error');
    alert(error || 'Payment failed. Please try again.');
  };

  const handleScrollToEnroll = () => {
    const enrollSection = document.getElementById('enroll-section');
    if (enrollSection) {
      enrollSection.scrollIntoView({ behavior: 'smooth' });
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="animate-pulse">
            <div className="h-8 bg-gray-300 rounded w-1/4 mb-6"></div>
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              <div className="lg:col-span-2">
                <div className="bg-gray-300 h-64 rounded-lg mb-6"></div>
                <div className="h-6 bg-gray-300 rounded w-3/4 mb-4"></div>
                <div className="h-4 bg-gray-300 rounded w-1/2 mb-6"></div>
                <div className="space-y-2">
                  <div className="h-4 bg-gray-300 rounded"></div>
                  <div className="h-4 bg-gray-300 rounded w-5/6"></div>
                  <div className="h-4 bg-gray-300 rounded w-4/6"></div>
                </div>
              </div>
              <div className="lg:col-span-1">
                <div className="bg-white rounded-lg shadow-md p-6">
                  <div className="h-6 bg-gray-300 rounded mb-4"></div>
                  <div className="h-10 bg-gray-300 rounded mb-4"></div>
                  <div className="space-y-3">
                    <div className="h-4 bg-gray-300 rounded"></div>
                    <div className="h-4 bg-gray-300 rounded"></div>
                    <div className="h-4 bg-gray-300 rounded"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !course) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="mb-4">
            <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.996-.833-2.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">Course not found</h3>
          <p className="text-gray-600 mb-4">{error || 'The course you are looking for does not exist.'}</p>
          <Link
            to="/courses"
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-blue-600 bg-blue-100 hover:bg-blue-200"
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Courses
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Breadcrumb */}
        <nav className="flex items-center space-x-2 text-sm text-gray-500 mb-6">
          <Link to="/courses" className="hover:text-blue-600">Courses</Link>
          <span>/</span>
          <span className="text-gray-900">{course.courseTitle}</span>
        </nav>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Course Video/Image */}
            <div className="relative mb-6 rounded-lg overflow-hidden">
              {showVideo && videoId ? (
                <div className="relative aspect-video bg-black rounded-lg overflow-hidden">
                  <YouTube
                    videoId={videoId}
                    opts={{
                      height: '100%',
                      width: '100%',
                      playerVars: {
                        autoplay: 1,
                        modestbranding: 1,
                        rel: 0,
                        controls: 1,
                        showinfo: 0,
                        fs: 1,
                        cc_load_policy: 0,
                        iv_load_policy: 3,
                        autohide: 0
                      }
                    }}
                    onReady={(event) => {
                      console.log('YouTube player ready');
                    }}
                    onError={(error) => {
                      console.error('YouTube player error:', error);
                      setShowVideo(false);
                    }}
                    style={{
                      width: '100%',
                      height: '100%'
                    }}
                    iframeClassName="w-full h-full"
                  />
                  <button
                    onClick={() => setShowVideo(false)}
                    className="absolute top-3 right-3 bg-black bg-opacity-50 text-white px-3 py-1 rounded text-sm hover:bg-opacity-70 transition-all z-10"
                  >
                    Show Thumbnail
                  </button>
                </div>
              ) : (
                <div className="relative aspect-video rounded-lg overflow-hidden">
                  {imageProps ? (
                    <img 
                      {...imageProps}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <img 
                      src={assets.course_1_thumbnail}
                      alt="Course thumbnail"
                      className="w-full h-full object-cover"
                    />
                  )}
                  <div className="absolute inset-0 bg-black bg-opacity-20"></div>
                  <div className="absolute inset-0 flex items-center justify-center">
                    <button 
                      onClick={() => {
                        if (videoId) {
                          setShowVideo(true);
                        } else {
                          alert('No video available for this course');
                        }
                      }}
                      className="bg-red-600 hover:bg-red-700 rounded-full p-4 transition-all group shadow-lg"
                    >
                      <svg 
                        className="h-8 w-8 text-white ml-1 group-hover:scale-110 transition-transform" 
                        fill="currentColor" 
                        viewBox="0 0 24 24"
                      >
                        <path d="M8 5v14l11-7z"/>
                      </svg>
                    </button>
                  </div>
                  {videoId && (
                    <div className="absolute top-3 left-3 bg-red-600 text-white px-2 py-1 rounded text-xs font-semibold flex items-center space-x-1">
                      <svg className="h-3 w-3 fill-current" viewBox="0 0 24 24">
                        <path d="M8 5v14l11-7z"/>
                      </svg>
                      <span>Video Available</span>
                    </div>
                  )}
                  {!videoId && (
                    <div className="absolute top-3 left-3 bg-gray-600 text-white px-2 py-1 rounded text-xs font-semibold flex items-center space-x-1">
                      <BookOpen className="h-3 w-3" />
                      <span>Course Preview</span>
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* Course Title & Instructor */}
            <div className="mb-6">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{course.courseTitle}</h1>
              <div 
                className="text-lg text-gray-600 mb-4"
                dangerouslySetInnerHTML={{ 
                  __html: (course.courseDescription || '').replace(/<[^>]*>/g, '').slice(0, 200) + '...' 
                }}
              />
              <div className="flex items-center space-x-4 text-sm text-gray-600">
                <span>Created by <span className="font-medium text-gray-900">{getUserDisplayName(course.educator)}</span></span>
                <span>•</span>
                <div className="flex items-center space-x-1">
                  <Star className="h-4 w-4 text-yellow-400 fill-current" />
                  <span className="font-medium">{averageRating}</span>
                  <span>rating</span>
                </div>
                <span>•</span>
                <div className="flex items-center space-x-1">
                  <Users className="h-4 w-4" />
                  <span>{enrolledCount} students</span>
                </div>
              </div>
            </div>

            {/* Navigation Tabs */}
            <div className="border-b border-gray-200 mb-6">
              <nav className="-mb-px flex space-x-8">
                {[
                  { id: 'overview', label: 'Overview' },
                  { id: 'curriculum', label: 'Curriculum' },
                  { id: 'instructor', label: 'Instructor' },
                  { id: 'reviews', label: 'Reviews' }
                ].map((tab) => (
                  <button
                    key={tab.id}
                    onClick={() => setSelectedSection(tab.id)}
                    className={`py-2 px-1 border-b-2 font-medium text-sm ${
                      selectedSection === tab.id
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    }`}
                  >
                    {tab.label}
                  </button>
                ))}
              </nav>
            </div>

            {/* Tab Content */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              {selectedSection === 'overview' && (
                <div>
                  <h2 className="text-xl font-semibold text-gray-900 mb-4">Course Description</h2>
                  <div 
                    className="prose max-w-none text-gray-700 mb-6"
                    dangerouslySetInnerHTML={{ 
                      __html: course.courseDescription || 'No description available.' 
                    }}
                  />

                  <h3 className="text-lg font-semibold text-gray-900 mb-4">What you'll learn</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-6">
                    {course.whatYouWillLearn ? course.whatYouWillLearn.map((item, index) => (
                      <div key={index} className="flex items-start space-x-2">
                        <Check className="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                        <span className="text-gray-700">{item}</span>
                      </div>
                    )) : [
                      'Master the fundamentals and advanced concepts',
                      'Build real-world projects from scratch',
                      'Learn industry best practices and standards',
                      'Get hands-on experience with practical exercises',
                      'Develop problem-solving skills',
                      'Access to course materials and resources'
                    ].map((item, index) => (
                      <div key={index} className="flex items-start space-x-2">
                        <Check className="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                        <span className="text-gray-700">{item}</span>
                      </div>
                    ))}
                  </div>

                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Requirements</h3>
                  <ul className="space-y-2 text-gray-700">
                    {course.requirements ? course.requirements.map((req, index) => (
                      <li key={index}>• {req}</li>
                    )) : (
                      <>
                        <li>• Basic computer knowledge</li>
                        <li>• Internet connection for accessing course materials</li>
                        <li>• Willingness to learn and practice</li>
                      </>
                    )}
                  </ul>
                </div>
              )}

              {selectedSection === 'curriculum' && (
                <div>
                  <h2 className="text-xl font-semibold text-gray-900 mb-4">Course Curriculum</h2>
                  <div className="space-y-4">
                    {course.courseContent && course.courseContent.length > 0 ? (
                      course.courseContent.map((chapter, index) => (
                        <div key={chapter.chapterId || index} className="border border-gray-200 rounded-lg">
                          <div className="p-4 bg-gray-50 border-b border-gray-200">
                            <h3 className="font-medium text-gray-900">{chapter.chapterTitle}</h3>
                            <p className="text-sm text-gray-600 mt-1">
                              {chapter.chapterContent ? chapter.chapterContent.length : 0} lessons • 
                              {chapter.chapterContent ? 
                                chapter.chapterContent.reduce((total, lecture) => total + (lecture.duration || 10), 0) 
                                : 30} min
                            </p>
                          </div>
                          <div className="p-4 space-y-3">
                            {chapter.chapterContent && chapter.chapterContent.length > 0 ? (
                              chapter.chapterContent.map((lecture, lectureIndex) => {
                                const lectureVideoId = extractYouTubeId(lecture.lectureUrl);
                                return (
                                  <div key={lecture.lectureId || lectureIndex} className="flex items-center justify-between group hover:bg-gray-50 p-2 rounded">
                                    <div className="flex items-center space-x-3">
                                      {lectureVideoId ? (
                                        <svg className="h-4 w-4 text-red-500 fill-current" viewBox="0 0 24 24">
                                          <path d="M8 5v14l11-7z"/>
                                        </svg>
                                      ) : (
                                        <svg className="h-4 w-4 text-gray-400 fill-current" viewBox="0 0 24 24">
                                          <path d="M8 5v14l11-7z"/>
                                        </svg>
                                      )}
                                      <span className="text-gray-700">{lecture.lectureTitle || `Lecture ${lectureIndex + 1}`}</span>
                                      {lectureVideoId && (
                                        <button 
                                          onClick={() => {
                                            // You can implement individual lecture video playback here
                                            console.log('Play lecture video:', lectureVideoId);
                                            // For now, just play the video in the main player
                                            setShowVideo(true);
                                          }}
                                          className="opacity-0 group-hover:opacity-100 text-blue-600 text-xs hover:underline transition-opacity"
                                        >
                                          Preview
                                        </button>
                                      )}
                                      {lecture.isPreviewFree && (
                                        <span className="bg-green-100 text-green-800 text-xs px-2 py-1 rounded">
                                          Free Preview
                                        </span>
                                      )}
                                    </div>
                                    <span className="text-sm text-gray-500">{lecture.lectureDuration || lecture.duration || 10} min</span>
                                  </div>
                                );
                              })
                            ) : (
                              <div className="text-gray-500 text-sm">No lectures available</div>
                            )}
                          </div>
                        </div>
                      ))
                    ) : (
                      // Fallback content if no courseContent
                      Array.from({ length: 3 }, (_, index) => (
                        <div key={index} className="border border-gray-200 rounded-lg">
                          <div className="p-4 bg-gray-50 border-b border-gray-200">
                            <h3 className="font-medium text-gray-900">Section {index + 1}: Getting Started</h3>
                            <p className="text-sm text-gray-600 mt-1">{Math.floor(Math.random() * 5) + 3} lessons • {Math.floor(Math.random() * 60) + 30} min</p>
                          </div>
                          <div className="p-4 space-y-3">
                            {Array.from({ length: 3 }, (_, lessonIndex) => (
                              <div key={lessonIndex} className="flex items-center justify-between">
                                <div className="flex items-center space-x-3">
                                  <Play className="h-4 w-4 text-gray-400" />
                                  <span className="text-gray-700">Lesson {lessonIndex + 1}: Introduction to Concepts</span>
                                </div>
                                <span className="text-sm text-gray-500">{Math.floor(Math.random() * 15) + 5} min</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}

              {selectedSection === 'instructor' && (
                <div>
                  <h2 className="text-xl font-semibold text-gray-900 mb-4">About the Instructor</h2>
                  <div className="flex items-start space-x-4 mb-6">
                    <img 
                      src={assets.profile_img}
                      alt={course.educator || 'Instructor'}
                      className="h-20 w-20 rounded-full object-cover"
                    />
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">{course.educator || 'Anonymous'}</h3>
                      <p className="text-gray-600 mb-2">Professional Instructor & Developer</p>
                      <div className="flex items-center space-x-4 text-sm text-gray-600">
                        <div className="flex items-center space-x-1">
                          <Star className="h-4 w-4" />
                          <span>{averageRating} rating</span>
                        </div>
                        <div className="flex items-center space-x-1">
                          <Users className="h-4 w-4" />
                          <span>{enrolledCount}+ students</span>
                        </div>
                        <div className="flex items-center space-x-1">
                          <BookOpen className="h-4 w-4" />
                          <span>Multiple courses</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <p className="text-gray-700">
                    Expert instructor with experience in the field. 
                    Passionate about teaching and helping students achieve their goals through practical, hands-on learning.
                  </p>
                </div>
              )}

              {selectedSection === 'reviews' && (
                <div>
                  <h2 className="text-xl font-semibold text-gray-900 mb-4">Student Reviews</h2>
                  <div className="mb-6">
                    <div className="flex items-center space-x-2 mb-2">
                      <div className="flex items-center">
                        {[...Array(5)].map((_, index) => (
                          <Star 
                            key={index} 
                            className={`h-5 w-5 ${
                              index < Math.floor(averageRating) 
                                ? 'text-yellow-400 fill-current' 
                                : 'text-gray-300'
                            }`} 
                          />
                        ))}
                      </div>
                      <span className="text-lg font-semibold">{averageRating}</span>
                      <span className="text-gray-600">course rating</span>
                      <span className="text-gray-500">({course.courseRatings ? course.courseRatings.length : 0} reviews)</span>
                    </div>
                  </div>
                  
                  <div className="space-y-6">
                    {course.courseRatings && course.courseRatings.length > 0 ? (
                      course.courseRatings.slice(0, 5).map((review, index) => (
                        <div key={review._id || index} className="border-b border-gray-200 pb-6">
                          <div className="flex items-start space-x-4">
                            <img 
                              src={assets.profile_img}
                              alt="Student"
                              className="h-10 w-10 rounded-full object-cover"
                            />
                            <div className="flex-1">
                              <div className="flex items-center space-x-2 mb-1">
                                <span className="font-medium text-gray-900">{getUserDisplayName(review.userId)}</span>
                                <div className="flex items-center">
                                  {[...Array(5)].map((_, starIndex) => (
                                    <Star 
                                      key={starIndex} 
                                      className={`h-4 w-4 ${
                                        starIndex < review.rating 
                                          ? 'text-yellow-400 fill-current' 
                                          : 'text-gray-300'
                                      }`} 
                                    />
                                  ))}
                                </div>
                                {review.createdAt && (
                                  <span className="text-sm text-gray-500">
                                    {new Date(review.createdAt).toLocaleDateString()}
                                  </span>
                                )}
                              </div>
                              <p className="text-gray-700">
                                {review.comment || review.review || 'Great course! The instructor explains everything clearly and the hands-on projects really help to understand the concepts. Highly recommended for anyone looking to learn this topic.'}
                              </p>
                            </div>
                          </div>
                        </div>
                      ))
                    ) : (
                      // Fallback reviews if no real reviews
                      [...Array(3)].map((_, index) => (
                        <div key={index} className="border-b border-gray-200 pb-6">
                          <div className="flex items-start space-x-4">
                            <img 
                              src={assets.profile_img}
                              alt="Student"
                              className="h-10 w-10 rounded-full object-cover"
                            />
                            <div className="flex-1">
                              <div className="flex items-center space-x-2 mb-1">
                                <span className="font-medium text-gray-900">Student {index + 1}</span>
                                <div className="flex items-center">
                                  {[...Array(5)].map((_, starIndex) => (
                                    <Star key={starIndex} className="h-4 w-4 text-yellow-400 fill-current" />
                                  ))}
                                </div>
                              </div>
                              <p className="text-gray-700">
                                Great course! The instructor explains everything clearly and the hands-on projects 
                                really help to understand the concepts. Highly recommended for anyone looking to 
                                learn this topic.
                              </p>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-md p-6 sticky top-4" id="enroll-section">
              {/* Payment Status Messages */}
              {paymentStatus === 'success' && (
                <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg">
                  <div className="flex items-center space-x-2">
                    <Check className="h-5 w-5 text-green-600" />
                    <span className="text-green-800 font-medium">Payment Successful!</span>
                  </div>
                  <p className="text-green-700 text-sm mt-1">
                    You are now enrolled in this course.
                  </p>
                </div>
              )}
              
              {paymentStatus === 'cancelled' && (
                <div className="mb-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                  <div className="flex items-center space-x-2">
                    <AlertCircle className="h-5 w-5 text-yellow-600" />
                    <span className="text-yellow-800 font-medium">Payment Cancelled</span>
                  </div>
                  <p className="text-yellow-700 text-sm mt-1">
                    Your payment was cancelled. You can try again anytime.
                  </p>
                </div>
              )}

              {/* Enrollment Status */}
              <EnrollmentStatus 
                courseId={id} 
                onEnroll={handleScrollToEnroll}
                className="mb-6"
              />

              {/* Payment Button */}
              <PaymentButton
                course={course}
                onSuccess={handlePaymentSuccess}
                onError={handlePaymentError}
                className="mb-6"
                showDebugMode={isDebugMode}
              />

              {/* Course Includes */}
              <div className="border-t border-gray-200 pt-6">
                <h3 className="font-semibold text-gray-900 mb-4">This course includes:</h3>
                <div className="space-y-3">
                  <div className="flex items-center space-x-3">
                    <Clock className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">{totalDuration} on-demand video</span>
                  </div>
                  <div className="flex items-center space-x-3">
                    <BookOpen className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">{totalLectures} lessons</span>
                  </div>
                  {videoId && (
                    <div className="flex items-center space-x-3">
                      <Play className="h-5 w-5 text-red-500" />
                      <span className="text-gray-700">HD video content</span>
                    </div>
                  )}
                  <div className="flex items-center space-x-3">
                    <Award className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">Certificate of completion</span>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Globe className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">Access on mobile and TV</span>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Target className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">Full lifetime access</span>
                  </div>
                </div>
              </div>

              {/* Course Level */}
              <div className="border-t border-gray-200 pt-6 mt-6">
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">Level:</span>
                  <span className="font-medium text-gray-900">{course.level || 'Beginner'}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CourseDetail;


