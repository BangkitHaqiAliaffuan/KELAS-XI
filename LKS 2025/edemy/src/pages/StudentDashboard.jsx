import React from 'react';
import { useUser } from '@clerk/clerk-react';
import { BookOpen, Clock, Award, TrendingUp, Play, RefreshCw } from 'lucide-react';
import { useStudentDashboard } from '../hooks/useStudentDashboard.js';
import { Link } from 'react-router-dom';

const StatCard = ({ icon: Icon, label, value, color = "blue" }) => {
  const colorClasses = {
    blue: "text-blue-600",
    green: "text-green-600", 
    yellow: "text-yellow-600",
    purple: "text-purple-600"
  };

  return (
    <div className="bg-white rounded-lg shadow-sm p-6">
      <div className="flex items-center">
        <Icon className={`h-8 w-8 ${colorClasses[color]}`} />
        <div className="ml-4">
          <p className="text-sm font-medium text-gray-600">{label}</p>
          <p className="text-2xl font-bold text-gray-900">{value}</p>
        </div>
      </div>
    </div>
  );
};

const CourseProgress = ({ course }) => {
  return (
    <div className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:shadow-md transition-shadow">
      <div className="flex items-center space-x-4">
        <div className="w-16 h-12 bg-gradient-to-br from-blue-100 to-blue-200 rounded-lg flex items-center justify-center">
          <img 
            src={course.courseImage} 
            alt={course.courseTitle}
            className="w-14 h-10 object-cover rounded"
            onError={(e) => {
              e.target.style.display = 'none';
              e.target.nextSibling.style.display = 'flex';
            }}
          />
          <BookOpen className="h-6 w-6 text-blue-600 hidden" />
        </div>
        <div>
          <h3 className="font-medium text-gray-900">{course.courseTitle}</h3>
          <p className="text-sm text-gray-600">
            {course.currentChapter}: {course.currentLecture}
          </p>
        </div>
      </div>
      <div className="flex items-center space-x-4">
        <div className="w-32 bg-gray-200 rounded-full h-2">
          <div 
            className="bg-blue-600 h-2 rounded-full transition-all duration-500" 
            style={{ width: `${course.progress}%` }}
          ></div>
        </div>
        <span className="text-sm text-gray-600 w-12">{course.progress}%</span>
        <Link 
          to={`/course/${course.courseId}`}
          className="bg-blue-600 text-white p-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Play className="h-4 w-4" />
        </Link>
      </div>
    </div>
  );
};

const RecommendedCourse = ({ course }) => {
  return (
    <div className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
      <div className="w-full h-32 bg-gray-200 rounded-lg mb-4 overflow-hidden">
        <img 
          src={course.image} 
          alt={course.title}
          className="w-full h-full object-cover"
          onError={(e) => {
            e.target.style.display = 'none';
            e.target.nextSibling.style.display = 'flex';
          }}
        />
        <div className="w-full h-full bg-gradient-to-br from-gray-300 to-gray-400 hidden items-center justify-center">
          <BookOpen className="h-8 w-8 text-gray-600" />
        </div>
      </div>
      <h3 className="font-medium text-gray-900 mb-2 line-clamp-2">{course.title}</h3>
      <p className="text-sm text-gray-600 mb-3 line-clamp-2">{course.description}</p>
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          {course.originalPrice && (
            <span className="text-sm text-gray-500 line-through">${course.originalPrice}</span>
          )}
          <span className="text-lg font-bold text-blue-600">${course.price}</span>
          {course.discount && (
            <span className="bg-red-100 text-red-800 text-xs px-2 py-1 rounded">
              {course.discount}% OFF
            </span>
          )}
        </div>
      </div>
      <Link
        to={`/course/${course._id}`}
        className="mt-3 w-full bg-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-700 transition-colors text-center block"
      >
        View Course
      </Link>
    </div>
  );
};

const LoadingCard = () => (
  <div className="bg-white rounded-lg shadow-sm p-6 animate-pulse">
    <div className="flex items-center">
      <div className="h-8 w-8 bg-gray-300 rounded"></div>
      <div className="ml-4 space-y-2">
        <div className="h-4 bg-gray-300 rounded w-20"></div>
        <div className="h-6 bg-gray-300 rounded w-12"></div>
      </div>
    </div>
  </div>
);

const StudentDashboard = () => {
  const { user } = useUser();
  const { dashboardData, loading, error, refreshDashboard } = useStudentDashboard();

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Header */}
          <div className="mb-8">
            <div className="h-8 bg-gray-300 rounded w-64 mb-2 animate-pulse"></div>
            <div className="h-4 bg-gray-300 rounded w-96 animate-pulse"></div>
          </div>

          {/* Stats Cards Loading */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            {[...Array(4)].map((_, i) => (
              <LoadingCard key={i} />
            ))}
          </div>

          {/* Continue Learning Loading */}
          <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
            <div className="h-6 bg-gray-300 rounded w-48 mb-4 animate-pulse"></div>
            <div className="space-y-4">
              <div className="h-16 bg-gray-200 rounded-lg animate-pulse"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-600 text-xl mb-4">⚠️ Error Loading Dashboard</div>
          <p className="text-gray-600 mb-4">{error}</p>
          <button 
            onClick={refreshDashboard}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2 mx-auto"
          >
            <RefreshCw className="h-4 w-4" />
            <span>Retry</span>
          </button>
        </div>
      </div>
    );
  }

  if (!dashboardData) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-gray-600 text-xl mb-4">No dashboard data available</div>
          <button 
            onClick={refreshDashboard}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Refresh
          </button>
        </div>
      </div>
    );
  }

  const { stats, currentLearning, recommendedCourses } = dashboardData;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8 flex justify-between items-start">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">
              Welcome back, {dashboardData.user.name || user?.firstName || 'Student'}!
            </h1>
            <p className="text-gray-600 mt-2">
              Continue your learning journey and track your progress.
            </p>
          </div>
          <button 
            onClick={refreshDashboard}
            className="bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors flex items-center space-x-2"
          >
            <RefreshCw className="h-4 w-4" />
            <span>Refresh</span>
          </button>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatCard 
            icon={BookOpen} 
            label="Enrolled Courses" 
            value={stats.totalEnrolledCourses} 
            color="blue"
          />
          <StatCard 
            icon={Clock} 
            label="Hours Learned" 
            value={`${stats.totalHoursLearned}h`} 
            color="green"
          />
          <StatCard 
            icon={Award} 
            label="Completed Courses" 
            value={stats.completedCourses} 
            color="yellow"
          />
          <StatCard 
            icon={TrendingUp} 
            label="Average Progress" 
            value={`${stats.averageProgress}%`} 
            color="purple"
          />
        </div>

        {/* Continue Learning Section */}
        {currentLearning && currentLearning.length > 0 && (
          <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Continue Learning</h2>
            <div className="space-y-4">
              {currentLearning.map((course) => (
                <CourseProgress key={course._id} course={course} />
              ))}
            </div>
          </div>
        )}

        {/* Empty State for Continue Learning */}
        {(!currentLearning || currentLearning.length === 0) && (
          <div className="bg-white rounded-lg shadow-sm p-8 mb-8 text-center">
            <BookOpen className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <h2 className="text-xl font-bold text-gray-900 mb-2">No Active Courses</h2>
            <p className="text-gray-600 mb-4">
              You haven't enrolled in any courses yet. Start your learning journey today!
            </p>
            <Link 
              to="/courses"
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors inline-block"
            >
              Browse Courses
            </Link>
          </div>
        )}

        {/* Recommended Courses */}
        {recommendedCourses && recommendedCourses.length > 0 && (
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-900">Recommended for You</h2>
              <Link 
                to="/courses"
                className="text-blue-600 hover:text-blue-700 text-sm font-medium"
              >
                View All →
              </Link>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {recommendedCourses.slice(0, 6).map((course) => (
                <RecommendedCourse key={course._id} course={course} />
              ))}
            </div>
          </div>
        )}

        {/* No Recommended Courses */}
        {(!recommendedCourses || recommendedCourses.length === 0) && (
          <div className="bg-white rounded-lg shadow-sm p-8 text-center">
            <BookOpen className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <h2 className="text-xl font-bold text-gray-900 mb-2">No Recommendations Available</h2>
            <p className="text-gray-600">
              Check back later for personalized course recommendations.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDashboard;
