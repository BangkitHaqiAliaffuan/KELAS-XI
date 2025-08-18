import React from 'react';
import { assets, dummyCourses } from '../../assets/assets.js';

const Row = ({ course }) => {
  const students = course.enrolledStudents?.length || 0;
  const earnings = `$${(students * (course.coursePrice || 0)).toFixed(0)}`;
  const isLive = course.isPublished;
  return (
    <div className="grid grid-cols-12 items-center px-4 py-4 border-t text-sm">
      <div className="col-span-5 flex items-center gap-3">
        <img src={course.courseThumbnail} alt="thumb" className="h-10 w-16 rounded object-cover" />
        <div className="text-gray-700 line-clamp-1">{course.courseTitle}</div>
      </div>
      <div className="col-span-2 text-gray-700">{earnings}</div>
      <div className="col-span-2 text-gray-700">{students}</div>
      <div className="col-span-3">
        <div className="flex items-center gap-2">
          <label className="inline-flex items-center cursor-pointer">
            <input type="checkbox" className="sr-only" defaultChecked={isLive} />
            <div className={`w-10 h-5 rounded-full ${isLive ? 'bg-indigo-600' : 'bg-gray-300'}`}></div>
          </label>
          <span className="text-gray-700">{isLive ? 'Live' : 'Private'}</span>
        </div>
      </div>
    </div>
  );
};

const MyCourses = () => {
  return (
    <div>
      <h2 className="text-xl font-semibold text-gray-800 mb-4">My Courses</h2>
      <div className="bg-white border rounded-lg shadow-sm overflow-hidden">
        <div className="px-6 py-3 font-medium border-b">All Courses</div>
        <div className="grid grid-cols-12 px-4 py-2 bg-gray-50 text-xs font-medium text-gray-500">
          <div className="col-span-5">Course</div>
          <div className="col-span-2">Earnings</div>
          <div className="col-span-2">Students</div>
          <div className="col-span-3">Course Status</div>
        </div>
        {dummyCourses.slice(0,5).map((c) => (
          <Row key={c._id} course={c} />
        ))}
      </div>
    </div>
  );
};

export default MyCourses;


