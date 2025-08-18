import React, { useState } from 'react';
import { assets } from '../../assets/assets.js';

const Input = ({ label, placeholder, as = 'input', ...props }) => {
  return (
    <div className="space-y-2">
      <label className="text-sm text-gray-600">{label}</label>
      {as === 'textarea' ? (
        <textarea
          placeholder={placeholder}
          className="w-full rounded-md border px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
          rows={3}
          {...props}
        />
      ) : (
        <input
          placeholder={placeholder}
          className="w-full rounded-md border px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
          {...props}
        />
      )}
    </div>
  );
};

const AddCourse = () => {
  const [thumbnail, setThumbnail] = useState(assets.course_4_thumbnail);

  return (
    <div>
      <h2 className="text-xl font-semibold text-gray-800 mb-6">Add Course</h2>
      <div className="bg-white border rounded-lg p-6 shadow-sm">
        <div className="grid gap-5 max-w-3xl">
          <Input label="Course Title" placeholder="Type here" />
          <Input label="Course Headings" placeholder="Type here" />
          <Input label="Course Description" as="textarea" placeholder="Type here" />
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 items-end">
            <div>
              <Input label="Course Price" placeholder="0" type="number" />
            </div>
            <div className="sm:col-span-2">
              <label className="text-sm text-gray-600">Course Thumbnail</label>
              <div className="flex items-center gap-3 mt-2">
                <button className="px-3 py-2 border rounded-md text-sm hover:bg-gray-50">
                  <div className="flex items-center gap-2">
                    <img src={assets.file_upload_icon} className="h-4 w-4" alt="" />
                    <span>Upload</span>
                  </div>
                </button>
                <img src={thumbnail} className="h-10 rounded" alt="thumbnail" />
              </div>
            </div>
          </div>

          <div>
            <button className="px-5 py-2 bg-black text-white rounded-md text-sm">ADD</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddCourse;


