import React from 'react';
import EducatorPageGuard from '../../components/EducatorPageGuard.jsx';
import { assets, dummyDashboardData, dummyStudentEnrolled } from '../../assets/assets.js';

const StatCard = ({ icon, label, value }) => (
  <div className="flex items-center gap-4 bg-white border rounded-lg px-6 py-4 shadow-sm">
    <div className="h-10 w-10 rounded-lg bg-indigo-600 grid place-items-center">
      <img src={icon} className="h-5 w-5 invert" alt="" />
    </div>
    <div>
      <div className="text-sm text-gray-500">{label}</div>
      <div className="text-xl font-semibold">{value}</div>
    </div>
  </div>
);

const Dashboard = () => {
  return (
    <EducatorPageGuard pageName="EducatorDashboard">
      <div>
      <h2 className="text-xl font-semibold text-gray-800 mb-4">Dashboard</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <StatCard icon={assets.person_tick_icon} label="Total Enrolments" value={14} />
        <StatCard icon={assets.lesson_icon} label="Total Courses" value={dummyDashboardData.totalCourses} />
        <StatCard icon={assets.earning_icon} label="Total Earnings" value={`$${dummyDashboardData.totalEarnings}`} />
      </div>

      <div className="bg-white border rounded-lg shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b font-medium">Latest Enrolments</div>
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
    </EducatorPageGuard>
  );
};

export default Dashboard;


