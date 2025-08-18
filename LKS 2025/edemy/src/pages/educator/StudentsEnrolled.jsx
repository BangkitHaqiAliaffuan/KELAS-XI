import React from 'react';
import { dummyStudentEnrolled, assets } from '../../assets/assets.js';

const StudentsEnrolled = () => {
  return (
    <div>
      <h2 className="text-xl font-semibold text-gray-800 mb-4">Students Enrolled</h2>
      <div className="bg-white border rounded-lg shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-6 py-3 font-medium text-gray-500">#</th>
                <th className="text-left px-6 py-3 font-medium text-gray-500">Student name</th>
                <th className="text-left px-6 py-3 font-medium text-gray-500">Course Title</th>
                <th className="text-left px-6 py-3 font-medium text-gray-500">Date</th>
              </tr>
            </thead>
            <tbody>
              {dummyStudentEnrolled.map((row, idx) => (
                <tr key={idx} className="border-t">
                  <td className="px-6 py-3">{idx + 1}</td>
                  <td className="px-6 py-3">
                    <div className="flex items-center gap-3">
                      <img src={assets.profile_img_1} alt="" className="h-8 w-8 rounded-full" />
                      <span className="text-gray-700">{row.student.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-3 text-gray-700">{row.courseTitle}</td>
                  <td className="px-6 py-3 text-gray-500">22 Aug., 2024</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default StudentsEnrolled;


