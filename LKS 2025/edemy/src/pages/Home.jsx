import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { assets } from '../assets/assets.js';
import { useApp } from '../context/AppContext.jsx';
import CourseCard from '../components/CourseCard.jsx';

const Home = () => {
  const { courses, testimonials } = useApp();
  const featuredCourses = courses.slice(0, 4);

  return (
    <div>
      {/* Hero */}
      <section className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-10 items-center">
            <div>
              <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-gray-900 leading-tight">
                Learn the skills to accelerate your career
              </h1>
              <p className="mt-4 text-gray-600 text-base sm:text-lg">
                High‑quality courses taught by industry experts. Study at your own pace with practical lessons, projects, and a supportive community.
              </p>
              <div className="mt-8 flex flex-col sm:flex-row gap-3">
                <Link
                  to="/courses"
                  className="inline-flex items-center justify-center bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors"
                >
                  Browse Courses
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Link>
                <a
                  href="#featured"
                  className="inline-flex items-center justify-center border border-gray-300 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-50 transition-colors"
                >
                  View Featured
                </a>
              </div>
             
            </div>
            <div className="relative">
              <img
                src={assets.course_4_thumbnail}
                alt="Learning"
                className="w-full rounded-xl shadow-lg"
              />
              <div className="absolute -bottom-4 -left-4 bg-blue-600 text-white px-4 py-2 rounded-lg shadow-lg text-sm">
                Join thousands of learners
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Trusted by Industry */}
      <section id="trusted" className="bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">
          <div className="text-center mb-10">
            <span className="inline-block px-3 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-700 mb-3">
              Our Partners
            </span>
            <h2 className="text-2xl font-bold text-gray-900">Trusted by industry leaders</h2>
            <p className="text-gray-600 mt-2">Top companies rely on Edemy to upskill their teams</p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-6 items-center">
            {[assets.microsoft_logo, assets.walmart_logo, assets.accenture_logo, assets.adobe_logo, assets.paypal_logo].map((logoSrc, idx) => (
              <div
                key={idx}
                className="bg-white rounded-xl border border-gray-100 p-6 flex items-center justify-center shadow-sm hover:shadow-md transition-shadow"
              >
                <img src={logoSrc} alt="Partner logo" className="h-8 w-auto object-contain opacity-80" />
              </div>
            ))}
          </div>

          <div className="mt-10 grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="bg-white rounded-xl border border-gray-100 p-5 text-center">
              <div className="text-2xl font-bold text-gray-900">10k+</div>
              <div className="text-sm text-gray-600">Active learners</div>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-5 text-center">
              <div className="text-2xl font-bold text-gray-900">150+</div>
              <div className="text-sm text-gray-600">Expert instructors</div>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 p-5 text-center">
              <div className="text-2xl font-bold text-gray-900">500+</div>
              <div className="text-sm text-gray-600">Curated lessons</div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Courses */}
      <section id="featured" className="bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">
          <div className="flex items-end justify-between mb-6">
            <div>
              <h2 className="text-2xl font-bold text-gray-900">Featured Courses</h2>
              <p className="text-gray-600 mt-1">Hand‑picked to get you started</p>
            </div>
            <Link to="/courses" className="text-blue-600 hover:text-blue-700 font-medium">
              See all
            </Link>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {featuredCourses.map((course) => (
              <CourseCard key={course._id} course={course} />
            ))}
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">What learners say</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {testimonials.slice(0, 3).map((t, idx) => (
              <div key={idx} className="bg-gray-50 rounded-lg p-6 border border-gray-100">
                <div className="flex items-center gap-4">
                  <img src={t.image} alt={t.name} className="h-12 w-12 rounded-full object-cover" />
                  <div>
                    <div className="font-semibold text-gray-900">{t.name}</div>
                    <div className="text-sm text-gray-600">{t.role}</div>
                  </div>
                </div>
                <p className="mt-4 text-gray-700 text-sm leading-relaxed">{t.feedback}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="bg-blue-600">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 text-center text-white">
          <h3 className="text-2xl sm:text-3xl font-bold">Ready to start learning?</h3>
          <p className="mt-2 text-blue-100">Browse our catalog and enroll in your first course today.</p>
          <Link
            to="/courses"
            className="inline-flex items-center justify-center mt-6 bg-white text-blue-700 px-6 py-3 rounded-lg font-semibold hover:bg-blue-50 transition-colors"
          >
            Explore Courses
            <ArrowRight className="ml-2 h-5 w-5" />
          </Link>
        </div>
      </section>
    </div>
  );
};

export default Home;


