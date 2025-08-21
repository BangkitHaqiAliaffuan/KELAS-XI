import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { assets } from '../assets/assets.js';
import { courseAPI, testimonialAPI } from '../services/api.js';
import CourseCard from '../components/CourseCard.jsx';

const Home = () => {
  const [featuredCourses, setFeaturedCourses] = useState([]);
  const [testimonials, setTestimonials] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch featured courses and testimonials in parallel
        const [coursesResponse, testimonialsResponse] = await Promise.all([
          courseAPI.getFeaturedCourses(),
          testimonialAPI.getFeaturedTestimonials()
        ]);

        console.log('Featured courses response:', coursesResponse);
        console.log('Testimonials response:', testimonialsResponse);
        console.log('Testimonials response.data:', testimonialsResponse.data);
        console.log('Testimonials response.success:', testimonialsResponse.success);
        console.log('Type of testimonials data:', typeof testimonialsResponse.data);
        console.log('Is testimonials data array?', Array.isArray(testimonialsResponse.data));
        
        if (testimonialsResponse.data && testimonialsResponse.data.length > 0) {
          console.log('First testimonial:', testimonialsResponse.data[0]);
          console.log('Testimonial fields:', Object.keys(testimonialsResponse.data[0]));
        }

        setFeaturedCourses(coursesResponse.data || []);
        setTestimonials(testimonialsResponse.data || []);
      } catch (err) {
        console.error('Error fetching homepage data:', err);
        setError('Failed to load content. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <div>
      {/* Error State */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg mb-4 mx-4">
          {error}
        </div>
      )}

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
            {loading ? (
              // Loading skeleton
              Array.from({ length: 4 }).map((_, idx) => (
                <div key={idx} className="bg-white rounded-lg border border-gray-200 overflow-hidden animate-pulse">
                  <div className="bg-gray-300 h-48 w-full"></div>
                  <div className="p-4">
                    <div className="h-4 bg-gray-300 rounded mb-2"></div>
                    <div className="h-3 bg-gray-300 rounded w-3/4 mb-3"></div>
                    <div className="h-4 bg-gray-300 rounded w-1/2"></div>
                  </div>
                </div>
              ))
            ) : featuredCourses.length > 0 ? (
              featuredCourses.map((course) => (
                <CourseCard key={course._id} course={course} />
              ))
            ) : (
              <div className="col-span-full text-center py-8 text-gray-500">
                No featured courses available at the moment.
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">
          <div className="text-center mb-10">
            <h2 className="text-2xl font-bold text-gray-900 mb-4">Testimonials</h2>
            <p className="text-gray-600">
              Hear from our learners as they share their journeys of transformation, success, and how our 
              platform has made a difference in their lives.
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {loading ? (
              // Loading skeleton for testimonials
              Array.from({ length: 3 }).map((_, idx) => (
                <div key={idx} className="bg-gray-50 rounded-lg p-6 border border-gray-100 animate-pulse">
                  <div className="flex items-center gap-4 mb-4">
                    <div className="h-12 w-12 rounded-full bg-gray-300"></div>
                    <div>
                      <div className="h-4 bg-gray-300 rounded w-24 mb-1"></div>
                      <div className="h-3 bg-gray-300 rounded w-16"></div>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <div className="h-3 bg-gray-300 rounded"></div>
                    <div className="h-3 bg-gray-300 rounded w-5/6"></div>
                    <div className="h-3 bg-gray-300 rounded w-4/6"></div>
                  </div>
                </div>
              ))
            ) : testimonials.length > 0 ? (
              console.log('🎯 Rendering testimonials:', testimonials.length, 'items') ||
              testimonials.slice(0, 3).map((testimonial, idx) => {
                console.log(`🎯 Rendering testimonial ${idx}:`, testimonial);
                return (
                <div key={testimonial._id || idx} className="bg-gray-50 rounded-lg p-6 border border-gray-100">
                  <div className="flex items-center gap-4">
                    <img 
                      src={testimonial.image || testimonial.userImage} 
                      alt={testimonial.name || testimonial.userName} 
                      className="h-12 w-12 rounded-full object-cover"
                      onError={(e) => {
                        e.target.src = assets.profile_img; // Fallback image
                      }}
                    />
                    <div>
                      <div className="font-semibold text-gray-900">{testimonial.name || testimonial.userName}</div>
                      <div className="text-sm text-gray-600">{testimonial.role || testimonial.userDesignation || 'Verified Student'}</div>
                    </div>
                  </div>
                  <p className="mt-4 text-gray-700 text-sm leading-relaxed">{testimonial.feedback || testimonial.testimonialText || testimonial.text}</p>
                  <div className="mt-3 flex items-center justify-between">
                    <div className="flex items-center">
                      {[...Array(5)].map((_, starIdx) => (
                        <svg
                          key={starIdx}
                          className={`h-4 w-4 ${
                            starIdx < testimonial.rating ? 'text-yellow-400' : 'text-gray-300'
                          }`}
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      ))}
                    </div>
                    <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                      Read more
                    </button>
                  </div>
                </div>
                );
              })
            ) : (
              console.log('⚠️ No testimonials to render. Length:', testimonials.length) ||
              <div className="col-span-full text-center py-8 text-gray-500">
                No testimonials available at the moment.
              </div>
            )}
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


