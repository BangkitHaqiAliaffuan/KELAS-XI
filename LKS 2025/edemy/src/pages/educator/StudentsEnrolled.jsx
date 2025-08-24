import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { assets } from '../../assets/assets.js';

const StudentsEnrolled = () => {
  const { user } = useUser();
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pagination, setPagination] = useState({ total: 0, pages: 0 });

  useEffect(() => {
    const fetchEnrollments = async () => {
      if (!user?.id) return;

      try {
        setLoading(true);
        const response = await fetch(
          `http://localhost:5000/api/enrollments?instructorId=${user.id}&page=${currentPage}&limit=20&sortBy=enrollmentDate&sortOrder=desc`
        );
        const result = await response.json();

        if (result.success) {
          setEnrollments(result.data || []);
          setPagination(result.pagination || { total: 0, pages: 0 });
        } else {
          setError(result.message || 'Failed to fetch enrollments');
        }
      } catch (error) {
        console.error('Error fetching enrollments:', error);
        setError('Network error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchEnrollments();
  }, [user?.id, currentPage]);

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= pagination.pages) {
      setCurrentPage(newPage);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-800">Students Enrolled</h2>
        <div className="text-sm text-gray-500">
          {pagination.total} total enrollments
        </div>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-200 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      <div className="bg-white border rounded-lg shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-gray-500">
            Loading enrollments...
          </div>
        ) : enrollments.length === 0 ? (
          <div className="p-8 text-center text-gray-500">
            <p className="mb-4">No students enrolled yet.</p>
            <a 
              href="/dashboard/add-course" 
              className="text-indigo-600 hover:text-indigo-700 font-medium"
            >
              Create a course to get enrollments →
            </a>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="text-left px-6 py-3 font-medium text-gray-500">#</th>
                    <th className="text-left px-6 py-3 font-medium text-gray-500">Student Name</th>
                    <th className="text-left px-6 py-3 font-medium text-gray-500">Course Title</th>
                    <th className="text-left px-6 py-3 font-medium text-gray-500">Enrollment Date</th>
                    <th className="text-left px-6 py-3 font-medium text-gray-500">Amount Paid</th>
                    <th className="text-left px-6 py-3 font-medium text-gray-500">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {enrollments.map((enrollment, idx) => (
                    <tr key={enrollment._id} className="border-t hover:bg-gray-50">
                      <td className="px-6 py-3">
                        {(currentPage - 1) * 20 + idx + 1}
                      </td>
                      <td className="px-6 py-3">
                        <div className="flex items-center gap-3">
                          <img 
                            src={enrollment.student?.profileImage || assets.profile_img_1} 
                            alt="" 
                            className="h-8 w-8 rounded-full object-cover" 
                          />
                          <div>
                            <div className="text-gray-700 font-medium">
                              {enrollment.student?.name || 'Unknown Student'}
                            </div>
                            <div className="text-xs text-gray-500">
                              {enrollment.student?.email || ''}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-3">
                        <div className="text-gray-700">
                          {enrollment.course?.courseTitle || 'Unknown Course'}
                        </div>
                      </td>
                      <td className="px-6 py-3 text-gray-500">
                        {formatDate(enrollment.enrollmentDate)}
                      </td>
                      <td className="px-6 py-3">
                        <span className="text-green-600 font-medium">
                          ${enrollment.amountPaid?.toFixed(2) || '0.00'}
                        </span>
                      </td>
                      <td className="px-6 py-3">
                        <span className={`px-2 py-1 text-xs rounded-full ${
                          enrollment.paymentStatus === 'completed' 
                            ? 'bg-green-100 text-green-700'
                            : 'bg-yellow-100 text-yellow-700'
                        }`}>
                          {enrollment.paymentStatus || 'pending'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {pagination.pages > 1 && (
              <div className="flex items-center justify-between px-6 py-4 border-t bg-gray-50">
                <div className="text-sm text-gray-500">
                  Showing {(currentPage - 1) * 20 + 1} to {Math.min(currentPage * 20, pagination.total)} of {pagination.total} results
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage <= 1}
                    className="px-3 py-1 text-sm border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Previous
                  </button>
                  
                  {[...Array(Math.min(5, pagination.pages))].map((_, i) => {
                    const pageNum = Math.max(1, currentPage - 2) + i;
                    if (pageNum > pagination.pages) return null;
                    
                    return (
                      <button
                        key={pageNum}
                        onClick={() => handlePageChange(pageNum)}
                        className={`px-3 py-1 text-sm border rounded ${
                          currentPage === pageNum 
                            ? 'bg-indigo-600 text-white border-indigo-600' 
                            : 'hover:bg-gray-100'
                        }`}
                      >
                        {pageNum}
                      </button>
                    );
                  })}
                  
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage >= pagination.pages}
                    className="px-3 py-1 text-sm border rounded hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default StudentsEnrolled;


