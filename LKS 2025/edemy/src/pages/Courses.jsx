import React from 'react';
import { useApp } from '../context/AppContext.jsx';
import CourseCard from '../components/CourseCard.jsx';

const Courses = () => {
  const { filteredCourses, searchQuery } = useApp();

  return (
    <section className="bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="flex items-end justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">All Courses</h1>
            {searchQuery && (
              <p className="text-gray-600 mt-1">Search results for: <span className="font-semibold">{searchQuery}</span></p>
            )}
          </div>
        </div>

        {filteredCourses.length === 0 ? (
          <div className="text-center text-gray-600 py-20">
            No courses found.
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredCourses.map((course) => (
              <CourseCard key={course._id} course={course} />
            ))}
          </div>
        )}
      </div>
    </section>
  );
};

export default Courses;


