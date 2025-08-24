import React, { useState, useEffect } from 'react';
import { useUser } from '@clerk/clerk-react';
import { assets } from '../../assets/assets.js';
import { getImageProps } from '../../utils/imageUtils.js';

const Row = ({ course, onToggleStatus, index }) => {
  const students = course.enrolledStudents?.length || 0;
  const earnings = `$${(students * (course.coursePrice || 0)).toFixed(0)}`;
  const isLive = course.isPublished;
  
  // Get proper image props with fallback handling
  const imageProps = getImageProps(course.courseThumbnail, course.courseTitle, index);
  
  return (
    <div className="grid grid-cols-12 items-center px-4 py-4 border-t text-sm">
      <div className="col-span-5 flex items-center gap-3">
        <img 
          {...imageProps}
          className="h-10 w-16 rounded object-cover" 
        />
        <div>
          <div className="text-gray-700 line-clamp-1 font-medium">{course.courseTitle}</div>
          <div className="text-xs text-gray-500">{course.courseHeadings}</div>
        </div>
      </div>
      <div className="col-span-2 text-gray-700 font-medium">{earnings}</div>
      <div className="col-span-2 text-gray-700">{students}</div>
      <div className="col-span-3">
        <div className="flex items-center gap-2">
          <label className="inline-flex items-center cursor-pointer">
            <input 
              type="checkbox" 
              className="sr-only" 
              checked={isLive}
              onChange={() => onToggleStatus(course._id, !isLive)}
            />
            <div className={`w-10 h-5 rounded-full relative transition-colors ${
              isLive ? 'bg-indigo-600' : 'bg-gray-300'
            }`}>
              <div className={`w-4 h-4 bg-white rounded-full absolute top-0.5 transition-transform ${
                isLive ? 'translate-x-5' : 'translate-x-0.5'
              }`}></div>
            </div>
          </label>
          <span className="text-gray-700">{isLive ? 'Live' : 'Private'}</span>
        </div>
      </div>
    </div>
  );
};

const MyCourses = () => {
  const { user } = useUser();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Fetch courses created by this instructor
  useEffect(() => {
    const fetchMyCourses = async () => {
      if (!user?.id) return;

      try {
        setLoading(true);
        const response = await fetch(`http://localhost:5000/api/courses?instructorId=${user.id}`);
        const result = await response.json();

        if (result.success) {
          setCourses(result.data || []);
        } else {
          setError(result.message || 'Failed to fetch courses');
        }
      } catch (error) {
        console.error('Error fetching courses:', error);
        setError('Network error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchMyCourses();
  }, [user?.id]);

  // Toggle course publish status
  const handleToggleStatus = async (courseId, newStatus) => {
    try {
      const response = await fetch(`http://localhost:5000/api/courses/${courseId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ isPublished: newStatus })
      });

      const result = await response.json();

      if (result.success) {
        // Update local state
        setCourses(prev => prev.map(course => 
          course._id === courseId 
            ? { ...course, isPublished: newStatus }
            : course
        ));
      } else {
        console.error('Failed to update course status:', result.message);
      }
    } catch (error) {
      console.error('Error updating course status:', error);
    }
  };

  const totalEarnings = courses.reduce((sum, course) => {
    const students = course.enrolledStudents?.length || 0;
    return sum + (students * (course.coursePrice || 0));
  }, 0);

  const totalStudents = courses.reduce((sum, course) => {
    return sum + (course.enrolledStudents?.length || 0);
  }, 0);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-800">My Courses</h2>
        <div className="flex gap-6 text-sm">
          <div className="text-center">
            <div className="text-2xl font-bold text-indigo-600">{courses.length}</div>
            <div className="text-gray-500">Total Courses</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-green-600">${totalEarnings.toFixed(0)}</div>
            <div className="text-gray-500">Total Earnings</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-600">{totalStudents}</div>
            <div className="text-gray-500">Total Students</div>
          </div>
        </div>
      </div>

      <div className="bg-white border rounded-lg shadow-sm overflow-hidden">
        <div className="px-6 py-3 font-medium border-b">All Courses</div>
        
        {loading ? (
          <div className="p-8 text-center text-gray-500">
            Loading your courses...
          </div>
        ) : error ? (
          <div className="p-8 text-center text-red-500">
            {error}
          </div>
        ) : courses.length === 0 ? (
          <div className="p-8 text-center text-gray-500">
            <p className="mb-4">You haven't created any courses yet.</p>
            <a 
              href="/dashboard/add-course" 
              className="text-indigo-600 hover:text-indigo-700 font-medium"
            >
              Create your first course →
            </a>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-12 px-4 py-2 bg-gray-50 text-xs font-medium text-gray-500">
              <div className="col-span-5">Course</div>
              <div className="col-span-2">Earnings</div>
              <div className="col-span-2">Students</div>
              <div className="col-span-3">Course Status</div>
            </div>
            {courses.map((course, index) => (
              <Row 
                key={course._id} 
                course={course} 
                index={index}
                onToggleStatus={handleToggleStatus}
              />
            ))}
          </>
        )}
      </div>
    </div>
  );
};

export default MyCourses;


