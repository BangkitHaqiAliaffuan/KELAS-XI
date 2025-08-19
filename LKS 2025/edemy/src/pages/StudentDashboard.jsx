import React from 'react';
import { useUser } from '@clerk/clerk-react';
import { BookOpen, Clock, Award, TrendingUp } from 'lucide-react';

const StudentDashboard = () => {
  const { user } = useUser();

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Welcome back, {user?.firstName || 'Student'}!
          </h1>
          <p className="text-gray-600 mt-2">
            Continue your learning journey and track your progress.
          </p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <BookOpen className="h-8 w-8 text-blue-600" />
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Enrolled Courses</p>
                <p className="text-2xl font-bold text-gray-900">3</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <Clock className="h-8 w-8 text-green-600" />
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Hours Learned</p>
                <p className="text-2xl font-bold text-gray-900">24</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <Award className="h-8 w-8 text-yellow-600" />
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Certificates</p>
                <p className="text-2xl font-bold text-gray-900">1</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center">
              <TrendingUp className="h-8 w-8 text-purple-600" />
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Progress</p>
                <p className="text-2xl font-bold text-gray-900">75%</p>
              </div>
            </div>
          </div>
        </div>

        {/* Continue Learning Section */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Continue Learning</h2>
          <div className="space-y-4">
            <div className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <BookOpen className="h-6 w-6 text-blue-600" />
                </div>
                <div>
                  <h3 className="font-medium text-gray-900">Introduction to JavaScript</h3>
                  <p className="text-sm text-gray-600">Chapter 3: Variables and Data Types</p>
                </div>
              </div>
              <div className="flex items-center space-x-4">
                <div className="w-32 bg-gray-200 rounded-full h-2">
                  <div className="bg-blue-600 h-2 rounded-full" style={{ width: '60%' }}></div>
                </div>
                <span className="text-sm text-gray-600">60%</span>
              </div>
            </div>
          </div>
        </div>

        {/* Recommended Courses */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Recommended for You</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
              <div className="w-full h-32 bg-gray-200 rounded-lg mb-4"></div>
              <h3 className="font-medium text-gray-900 mb-2">Advanced Python Programming</h3>
              <p className="text-sm text-gray-600 mb-2">Take your Python skills to the next level</p>
              <div className="flex items-center justify-between">
                <span className="text-lg font-bold text-blue-600">$79.99</span>
                <button className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-700 transition-colors">
                  Enroll Now
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
