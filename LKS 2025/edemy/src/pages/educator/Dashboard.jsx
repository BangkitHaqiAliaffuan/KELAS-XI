import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { assets } from '../../assets/assets.js';

const StatCard = ({ icon, label, value, loading = false }) => (
  <div className="flex items-center gap-4 bg-white border rounded-lg px-6 py-4 shadow-sm">
    <div className="h-10 w-10 rounded-lg bg-indigo-600 grid place-items-center">
      <img src={icon} className="h-5 w-5 invert" alt="" />
    </div>
    <div>
      <div className="text-sm text-gray-500">{label}</div>
      <div className="text-xl font-semibold">
        {loading ? '...' : value}
      </div>
    </div>
  </div>
);

const Dashboard = () => {
  const { user } = useUser();
  const [dashboardData, setDashboardData] = useState({
    totalCourses: 0,
    totalEnrollments: 0,
    totalEarnings: 0,
    recentEnrollments: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboardData = async () => {
      if (!user?.id) return;

      try {
        setLoading(true);
        
        // Fetch instructor's courses
        const coursesResponse = await fetch(`http://localhost:5000/api/courses?instructorId=${user.id}`);
        const coursesResult = await coursesResponse.json();
        
        let totalCourses = 0;
        let totalEnrollments = 0;
        let totalEarnings = 0;
        
        if (coursesResult.success && coursesResult.data) {
          totalCourses = coursesResult.data.length;
          
          // Calculate total enrollments and earnings
          coursesResult.data.forEach(course => {
            const enrollments = course.enrolledStudents?.length || 0;
            totalEnrollments += enrollments;
            totalEarnings += enrollments * (course.coursePrice || 0);
          });
        }
        
        // Fetch recent enrollments for instructor's courses
        const enrollmentsResponse = await fetch(`http://localhost:5000/api/enrollments?instructorId=${user.id}&limit=10&sortBy=enrollmentDate&sortOrder=desc`);
        const enrollmentsResult = await enrollmentsResponse.json();
        
        const recentEnrollments = enrollmentsResult.success ? enrollmentsResult.data || [] : [];

        setDashboardData({
          totalCourses,
          totalEnrollments,
          totalEarnings,
          recentEnrollments
        });

      } catch (error) {
        console.error('Error fetching dashboard data:', error);
        setError('Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [user?.id]);

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-800">Dashboard</h2>
        {user && (
          <div className="text-sm text-gray-600">
            Welcome back, {user.fullName || `${user.firstName} ${user.lastName}`}!
          </div>
        )}
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-200 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <StatCard 
          icon={assets.person_tick_icon} 
          label="Total Enrollments" 
          value={dashboardData.totalEnrollments}
          loading={loading}
        />
        <StatCard 
          icon={assets.lesson_icon} 
          label="Total Courses" 
          value={dashboardData.totalCourses}
          loading={loading}
        />
        <StatCard 
          icon={assets.earning_icon} 
          label="Total Earnings" 
          value={`$${dashboardData.totalEarnings.toFixed(0)}`}
          loading={loading}
        />
      </div>

      <div className="bg-white border rounded-lg shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b font-medium">Latest Enrollments</div>
        {loading ? (
          <div className="p-8 text-center text-gray-500">
            Loading enrollments...
          </div>
        ) : dashboardData.recentEnrollments.length === 0 ? (
          <div className="p-8 text-center text-gray-500">
            <p className="mb-4">No enrollments yet.</p>
            <a 
              href="/dashboard/add-course" 
              className="text-indigo-600 hover:text-indigo-700 font-medium"
            >
              Create your first course to get enrollments →
            </a>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="bg-gray-50">
                <tr>
                  <th className="text-left px-6 py-3 font-medium text-gray-500">#</th>
                  <th className="text-left px-6 py-3 font-medium text-gray-500">Student Name</th>
                  <th className="text-left px-6 py-3 font-medium text-gray-500">Course Title</th>
                  <th className="text-left px-6 py-3 font-medium text-gray-500">Date</th>
                  <th className="text-left px-6 py-3 font-medium text-gray-500">Amount</th>
                </tr>
              </thead>
              <tbody>
                {dashboardData.recentEnrollments.map((enrollment, idx) => (
                  <tr key={enrollment._id} className="border-t">
                    <td className="px-6 py-3">{idx + 1}</td>
                    <td className="px-6 py-3">
                      <div className="flex items-center gap-3">
                        <img 
                          src={assets.profile_img_1} 
                          alt="" 
                          className="h-8 w-8 rounded-full object-cover" 
                        />
                        <span className="text-gray-700">
                          {enrollment.student?.name || enrollment.student?.email || 'Unknown Student'}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-3 text-gray-700">
                      {enrollment.course?.courseTitle || 'Unknown Course'}
                    </td>
                    <td className="px-6 py-3 text-gray-500">
                      {formatDate(enrollment.enrollmentDate)}
                    </td>
                    <td className="px-6 py-3 text-green-600 font-medium">
                      ${enrollment.amountPaid?.toFixed(2) || '0.00'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;


