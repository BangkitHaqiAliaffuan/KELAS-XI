import React, { useMemo } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Clock, Users, PlayCircle } from 'lucide-react';
import StarRating from '../components/StarRating.jsx';
import { useApp } from '../context/AppContext.jsx';
import { formatPrice, calculateTotalDuration, countTotalLectures, formatDuration } from '../utils/helpers.js';

const CourseDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getCourseById, getAverageRating, getDiscountedPrice, enrollInCourse, isEnrolled } = useApp();

  const course = getCourseById(id);

  const stats = useMemo(() => {
    if (!course) return { duration: '0m', lectures: 0, students: 0, rating: 0 };
    return {
      duration: calculateTotalDuration(course.courseContent),
      lectures: countTotalLectures(course.courseContent),
      students: course.enrolledStudents ? course.enrolledStudents.length : 0,
      rating: parseFloat(getAverageRating(course.courseRatings))
    };
  }, [course, getAverageRating]);

  if (!course) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-white rounded-lg p-8 text-center shadow">
          <h1 className="text-xl font-semibold text-gray-900">Course not found</h1>
          <p className="text-gray-600 mt-2">The course you are looking for does not exist.</p>
          <div className="mt-6">
            <Link to="/courses" className="text-blue-600 font-medium hover:underline">Back to all courses</Link>
          </div>
        </div>
      </div>
    );
  }

  const discountedPrice = getDiscountedPrice(course.coursePrice, course.discount);
  const enrolled = isEnrolled(course._id);

  const handleEnroll = () => {
    if (enrolled) return;
    enrollInCourse(course._id);
    navigate('/courses');
  };

  return (
    <div className="bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Title & meta */}
            <div>
              <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 leading-tight">
                {course.courseTitle}
              </h1>
              <p className="mt-3 text-gray-600">
                {/* Render a short plain-text summary */}
                {course.courseDescription.replace(/<[^>]*>/g, '').slice(0, 220)}
              </p>

              {/* Meta stats */}
              <div className="mt-4 flex flex-wrap items-center gap-x-6 gap-y-2 text-sm text-gray-600">
                <div className="flex items-center gap-1">
                  <StarRating rating={stats.rating} size={16} showRating={true} />
                </div>
                <div className="flex items-center gap-2">
                  <Clock className="h-4 w-4 text-gray-400" />
                  <span className="tabular-nums">{stats.duration}</span>
                </div>
                <div className="flex items-center gap-2">
                  <PlayCircle className="h-4 w-4 text-gray-400" />
                  <span className="tabular-nums">{stats.lectures} lectures</span>
                </div>
                <div className="flex items-center gap-2">
                  <Users className="h-4 w-4 text-gray-400" />
                  <span className="tabular-nums">{stats.students} students</span>
                </div>
              </div>
            </div>

            {/* Course Structure */}
            <div>
              <h2 className="text-lg font-semibold text-gray-900 mb-3">Course Structure</h2>
              <div className="text-xs text-gray-500 mb-3">
                {course.courseContent.length} sections • {stats.lectures} lectures • {stats.duration} total duration
              </div>
              <div className="divide-y divide-gray-200 border border-gray-200 rounded-lg overflow-hidden">
                {course.courseContent.map((chapter, cIdx) => (
                  <div key={chapter.chapterId || cIdx} className="">
                    <div className="bg-gray-50 px-4 py-3 flex items-center justify-between">
                      <div className="font-medium text-gray-900">
                        {chapter.chapterTitle}
                      </div>
                      <div className="text-xs text-gray-500">
                        {chapter.chapterContent.length} lectures
                      </div>
                    </div>
                    <div className="px-4 py-2 space-y-2">
                      {chapter.chapterContent.map((lecture, lIdx) => (
                        <div key={lecture.lectureId || lIdx} className="flex items-center justify-between text-sm text-gray-700">
                          <div className="flex items-center gap-3 min-w-0">
                            <PlayCircle className="h-4 w-4 text-gray-400 shrink-0" />
                            <span className="truncate">{lecture.lectureTitle}</span>
                          </div>
                          <span className="text-gray-500 tabular-nums ml-4 shrink-0">
                            {formatDuration(lecture.lectureDuration)}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Description */}
            <div>
              <h2 className="text-lg font-semibold text-gray-900 mb-3">Course Description</h2>
              <div
                className="prose prose-sm max-w-none text-gray-700"
                dangerouslySetInnerHTML={{ __html: course.courseDescription }}
              />
            </div>
          </div>

          {/* Sidebar */}
          <aside className="lg:col-span-1">
            <div className="sticky top-24 space-y-4">
              <div className="bg-white rounded-lg shadow border border-gray-100 overflow-hidden">
                <div className="aspect-video bg-gray-100">
                  <img src={course.courseThumbnail} alt={course.courseTitle} className="w-full h-full object-cover" />
                </div>
                <div className="p-4 space-y-4">
                  <div className="flex items-center gap-3">
                    {course.discount > 0 && (
                      <span className="text-xs font-semibold text-red-600 bg-red-50 px-2 py-1 rounded">
                        {course.discount}% OFF
                      </span>
                    )}
                  </div>
                  <div className="flex items-end gap-2">
                    <div className="text-2xl font-bold text-gray-900">{formatPrice(discountedPrice)}</div>
                    {course.discount > 0 && (
                      <div className="text-sm text-gray-500 line-through">{formatPrice(course.coursePrice)}</div>
                    )}
                  </div>
                  <div className="flex items-center gap-4 text-xs text-gray-600">
                    <div className="flex items-center gap-1"><Clock className="h-3 w-3" />{stats.duration}</div>
                    <div className="flex items-center gap-1"><PlayCircle className="h-3 w-3" />{stats.lectures} lectures</div>
                  </div>
                  <button
                    onClick={handleEnroll}
                    disabled={enrolled}
                    className={`w-full px-4 py-2 rounded-lg font-semibold transition-colors ${enrolled ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-blue-600 text-white hover:bg-blue-700'}`}
                  >
                    {enrolled ? 'Enrolled' : 'Enroll Now'}
                  </button>
                  <div className="border-t border-gray-100 pt-4">
                    <h3 className="text-sm font-semibold text-gray-900 mb-2">What's in this course?</h3>
                    <ul className="text-sm text-gray-600 space-y-1 list-disc list-inside">
                      <li>Lifetime access with free updates.</li>
                      <li>Hands-on projects and guidance.</li>
                      <li>Downloadable resources and source code.</li>
                      <li>Quizzes to test your knowledge.</li>
                      <li>Certificate of completion.</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </div>
  );
};

export default CourseDetail;


