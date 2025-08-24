import React, { useState } from 'react';
import { useUser } from '@clerk/clerk-react';
import { assets } from '../../assets/assets.js';

const Input = ({ label, placeholder, as = 'input', value, onChange, ...props }) => {
  return (
    <div className="space-y-2">
      <label className="text-sm text-gray-600">{label}</label>
      {as === 'textarea' ? (
        <textarea
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          className="w-full rounded-md border px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
          rows={3}
          {...props}
        />
      ) : (
        <input
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          className="w-full rounded-md border px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
          {...props}
        />
      )}
    </div>
  );
};

const AddCourse = () => {
  const { user } = useUser();
  const [formData, setFormData] = useState({
    courseTitle: '',
    courseHeadings: '',
    courseDescription: '',
    coursePrice: '',
    courseThumbnail: null
  });
  const [thumbnail, setThumbnail] = useState(assets.course_4_thumbnail);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleThumbnailUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      // Validasi file
      const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
      if (!validTypes.includes(file.type)) {
        setMessage({ type: 'error', text: 'Please select a valid image file (JPEG, PNG, GIF, WebP)' });
        return;
      }

      // Validasi ukuran file (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        setMessage({ type: 'error', text: 'Image size must be less than 5MB' });
        return;
      }

      // Convert file to base64
      const reader = new FileReader();
      reader.onload = (e) => {
        const base64String = e.target.result;
        setThumbnail(base64String); // Set preview
        setFormData(prev => ({
          ...prev,
          courseThumbnail: base64String // Simpan base64 string
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const createCourse = async () => {
    if (!formData.courseTitle || !formData.courseDescription || !formData.coursePrice) {
      setMessage({ type: 'error', text: 'Please fill in all required fields' });
      return;
    }

    if (!user) {
      setMessage({ type: 'error', text: 'You must be logged in to create a course' });
      return;
    }

    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      // Create course data object
      const courseData = {
        courseTitle: formData.courseTitle,
        courseHeadings: formData.courseHeadings,
        courseDescription: formData.courseDescription,
        coursePrice: parseFloat(formData.coursePrice),
        instructor: user.fullName || `${user.firstName} ${user.lastName}`,
        instructorId: user.id,
        instructorEmail: user.primaryEmailAddress?.emailAddress,
        courseThumbnail: formData.courseThumbnail || thumbnail, // Gunakan base64 string
        isPublished: false,
        createdAt: new Date().toISOString(),
        courseContent: [] // Start with empty content, can be added later
      };

      console.log('Creating course:', courseData);

      const response = await fetch('http://localhost:5000/api/courses', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(courseData)
      });

      const result = await response.json();

      if (result.success) {
        setMessage({ type: 'success', text: 'Course created successfully!' });
        
        // Reset form
        setFormData({
          courseTitle: '',
          courseHeadings: '',
          courseDescription: '',
          coursePrice: '',
          courseThumbnail: null
        });
        setThumbnail(assets.course_4_thumbnail);
        
        // Auto-clear success message after 3 seconds
        setTimeout(() => setMessage({ type: '', text: '' }), 3000);
      } else {
        setMessage({ type: 'error', text: result.message || 'Failed to create course' });
      }
    } catch (error) {
      console.error('Error creating course:', error);
      setMessage({ type: 'error', text: 'Network error occurred' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-800">Add Course</h2>
        {message.text && (
          <div className={`px-4 py-2 rounded-lg text-sm ${
            message.type === 'success' 
              ? 'bg-green-100 text-green-700 border border-green-200' 
              : 'bg-red-100 text-red-700 border border-red-200'
          }`}>
            {message.text}
          </div>
        )}
      </div>
      
      <div className="bg-white border rounded-lg p-6 shadow-sm">
        <div className="grid gap-5 max-w-3xl">
          <Input 
            label="Course Title *" 
            placeholder="Enter course title" 
            value={formData.courseTitle}
            onChange={(e) => handleInputChange('courseTitle', e.target.value)}
          />
          <Input 
            label="Course Headings" 
            placeholder="Short description" 
            value={formData.courseHeadings}
            onChange={(e) => handleInputChange('courseHeadings', e.target.value)}
          />
          <Input 
            label="Course Description *" 
            as="textarea" 
            placeholder="Describe your course in detail" 
            value={formData.courseDescription}
            onChange={(e) => handleInputChange('courseDescription', e.target.value)}
          />
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 items-end">
            <div>
              <Input 
                label="Course Price * (USD)" 
                placeholder="0" 
                type="number" 
                min="0"
                step="0.01"
                value={formData.coursePrice}
                onChange={(e) => handleInputChange('coursePrice', e.target.value)}
              />
            </div>
            <div className="sm:col-span-2">
              <label className="text-sm text-gray-600">Course Thumbnail</label>
              <div className="flex items-center gap-3 mt-2">
                <button 
                  onClick={() => document.getElementById('thumbnail-upload').click()}
                  className="px-3 py-2 border rounded-md text-sm hover:bg-gray-50"
                >
                  <div className="flex items-center gap-2">
                    <img src={assets.file_upload_icon} className="h-4 w-4" alt="" />
                    <span>Upload</span>
                  </div>
                </button>
                <img src={thumbnail} className="h-10 rounded" alt="thumbnail" />
                <input
                  id="thumbnail-upload"
                  type="file"
                  accept="image/*"
                  onChange={handleThumbnailUpload}
                  className="hidden"
                />
              </div>
            </div>
          </div>

          {user && (
            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="font-medium text-gray-700 mb-2">Course Preview</h3>
              <div className="text-sm text-gray-600 space-y-1">
                <p><strong>Title:</strong> {formData.courseTitle || 'Course Title'}</p>
                <p><strong>Price:</strong> ${formData.coursePrice || '0.00'}</p>
                <p><strong>Instructor:</strong> {user.fullName || `${user.firstName} ${user.lastName}`}</p>
              </div>
            </div>
          )}

          <div className="flex gap-3">
            <button 
              onClick={createCourse}
              disabled={loading}
              className={`px-5 py-2 rounded-md text-sm text-white transition-colors ${
                loading 
                  ? 'bg-gray-400 cursor-not-allowed' 
                  : 'bg-black hover:bg-gray-800'
              }`}
            >
              {loading ? 'Creating...' : 'CREATE COURSE'}
            </button>
            
            <button
              onClick={() => {
                setFormData({
                  courseTitle: '',
                  courseHeadings: '',
                  courseDescription: '',
                  coursePrice: '',
                  courseThumbnail: null
                });
                setThumbnail(assets.course_4_thumbnail);
                setMessage({ type: '', text: '' });
              }}
              className="px-5 py-2 border border-gray-300 text-gray-700 rounded-md text-sm hover:bg-gray-50 transition-colors"
            >
              RESET
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddCourse;


