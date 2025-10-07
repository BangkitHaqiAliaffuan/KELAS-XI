import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { X, Upload, MapPin, Building } from 'lucide-react';
import { cityService } from '../services/api';

const OfficeFormModal = ({ 
  isOpen, 
  onClose, 
  office = null, 
  onSubmit, 
  isLoading = false 
}) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    address: '',
    city_id: '',
    price_per_day: '',
    price_per_week: '',
    price_per_month: '',
    capacity: '',
    is_available: true,
    images: []
  });
  
  const [errors, setErrors] = useState({});
  const [imagePreview, setImagePreview] = useState(null);

  // Fetch cities from API
  const { data: citiesResponse } = useQuery({
    queryKey: ['cities'],
    queryFn: cityService.getAll,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  const cities = citiesResponse?.data || [];

  useEffect(() => {
    if (office) {
      
      setFormData({
        name: office.name || '',
        description: office.description || '',
        address: office.address || '',
        city_id: office.city?.id?.toString() || '',
        price_per_day: office.price_per_day?.toString() || office.price?.toString() || '',
        price_per_week: office.price_per_week?.toString() || '',
        price_per_month: office.price_per_month?.toString() || '',
        capacity: office.capacity?.toString() || '',
        is_available: office.is_available !== undefined ? office.is_available : true,
        images: []
      });
      setImagePreview(office.photos?.[0] || null);
    } else {
      setFormData({
        name: '',
        description: '',
        address: '',
        city_id: '',
        price_per_day: '',
        price_per_week: '',
        price_per_month: '',
        capacity: '',
        is_available: true,
        images: []
      });
      setImagePreview(null);
    }
    setErrors({});
  }, [office, isOpen]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    
    // Clear error when user types
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setFormData(prev => ({ ...prev, images: [file] }));
      
      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) newErrors.name = 'Office name is required';
    if (!formData.description.trim()) newErrors.description = 'Description is required';
    if (!formData.address.trim()) newErrors.address = 'Address is required';
    if (!formData.city_id) newErrors.city_id = 'City is required';
    if (!formData.price_per_day || formData.price_per_day <= 0) {
      newErrors.price_per_day = 'Valid daily price is required';
    }
    if (!formData.price_per_week || formData.price_per_week <= 0) {
      newErrors.price_per_week = 'Valid weekly price is required';
    }
    if (!formData.price_per_month || formData.price_per_month <= 0) {
      newErrors.price_per_month = 'Valid monthly price is required';
    }
    if (!formData.capacity || formData.capacity <= 0) {
      newErrors.capacity = 'Valid capacity is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    // Convert numeric strings to numbers
    const submitData = {
      ...formData,
      city_id: parseInt(formData.city_id),
      price_per_day: parseFloat(formData.price_per_day),
      price_per_week: parseFloat(formData.price_per_week),
      price_per_month: parseFloat(formData.price_per_month),
      capacity: parseInt(formData.capacity),
    };

    onSubmit(submitData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h3 className="text-lg font-medium text-gray-900 flex items-center">
            <Building className="h-5 w-5 mr-2 text-blue-600" />
            {office ? 'Edit Office' : 'Add New Office'}
          </h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="h-6 w-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="max-h-[calc(90vh-120px)] overflow-y-auto">
          <div className="px-6 py-4 space-y-6">
            {/* Image Upload */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Office Image
              </label>
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-6">
                {imagePreview ? (
                  <div className="relative">
                    <img 
                      src={imagePreview} 
                      alt="Preview" 
                      className="w-full h-48 object-cover rounded-md"
                    />
                    <button
                      type="button"
                      onClick={() => {
                        setImagePreview(null);
                        setFormData(prev => ({ ...prev, images: [] }));
                      }}
                      className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </div>
                ) : (
                  <div className="text-center">
                    <Upload className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <div className="text-sm text-gray-600">
                      <label htmlFor="image" className="cursor-pointer text-blue-600 hover:text-blue-500">
                        Upload an image
                      </label>
                      <input
                        id="image"
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        className="hidden"
                      />
                      <p className="pl-1">or drag and drop</p>
                    </div>
                    <p className="text-xs text-gray-500">PNG, JPG, GIF up to 10MB</p>
                  </div>
                )}
              </div>
            </div>

            {/* Office Name */}
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                Office Name *
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  errors.name ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="Enter office name"
              />
              {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name}</p>}
            </div>

            {/* Description */}
            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                Description *
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="4"
                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  errors.description ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="Describe the office space..."
              />
              {errors.description && <p className="text-red-500 text-sm mt-1">{errors.description}</p>}
            </div>

            {/* Address & City */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="address" className="block text-sm font-medium text-gray-700 mb-2">
                  Address *
                </label>
                <div className="relative">
                  <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                  <input
                    type="text"
                    id="address"
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    className={`w-full pl-10 pr-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                      errors.address ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder="Enter full address"
                  />
                </div>
                {errors.address && <p className="text-red-500 text-sm mt-1">{errors.address}</p>}
              </div>

              <div>
                <label htmlFor="city_id" className="block text-sm font-medium text-gray-700 mb-2">
                  City *
                </label>
                <select
                  id="city_id"
                  name="city_id"
                  value={formData.city_id}
                  onChange={handleChange}
                  className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                    errors.city_id ? 'border-red-500' : 'border-gray-300'
                  }`}
                >
                  <option value="">Select City</option>
                  {cities.map(city => (
                    <option key={city.id} value={city.id}>{city.name}</option>
                  ))}
                </select>
                {errors.city_id && <p className="text-red-500 text-sm mt-1">{errors.city_id}</p>}
              </div>
            </div>

            {/* Pricing */}
            <div>
              <h4 className="text-sm font-medium text-gray-700 mb-3">Pricing (IDR) *</h4>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label htmlFor="price_per_day" className="block text-sm font-medium text-gray-700 mb-2">
                    Daily Rate *
                  </label>
                  <input
                    type="number"
                    id="price_per_day"
                    name="price_per_day"
                    value={formData.price_per_day}
                    onChange={handleChange}
                    min="0"
                    className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                      errors.price_per_day ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder="500000"
                  />
                  {errors.price_per_day && <p className="text-red-500 text-sm mt-1">{errors.price_per_day}</p>}
                </div>

                <div>
                  <label htmlFor="price_per_week" className="block text-sm font-medium text-gray-700 mb-2">
                    Weekly Rate *
                  </label>
                  <input
                    type="number"
                    id="price_per_week"
                    name="price_per_week"
                    value={formData.price_per_week}
                    onChange={handleChange}
                    min="0"
                    className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                      errors.price_per_week ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder="3000000"
                  />
                  {errors.price_per_week && <p className="text-red-500 text-sm mt-1">{errors.price_per_week}</p>}
                </div>

                <div>
                  <label htmlFor="price_per_month" className="block text-sm font-medium text-gray-700 mb-2">
                    Monthly Rate *
                  </label>
                  <input
                    type="number"
                    id="price_per_month"
                    name="price_per_month"
                    value={formData.price_per_month}
                    onChange={handleChange}
                    min="0"
                    className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                      errors.price_per_month ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder="10000000"
                  />
                  {errors.price_per_month && <p className="text-red-500 text-sm mt-1">{errors.price_per_month}</p>}
                </div>
              </div>
            </div>

            {/* Capacity & Size */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="capacity" className="block text-sm font-medium text-gray-700 mb-2">
                  Capacity (people) *
                </label>
                <input
                  type="number"
                  id="capacity"
                  name="capacity"
                  value={formData.capacity}
                  onChange={handleChange}
                  min="1"
                  className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                    errors.capacity ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="10"
                />
                {errors.capacity && <p className="text-red-500 text-sm mt-1">{errors.capacity}</p>}
              </div>

              <div>
                <label htmlFor="size" className="block text-sm font-medium text-gray-700 mb-2">
                  Size (m²) *
                </label>
                <input
                  type="number"
                  id="size"
                  name="size"
                  value={formData.size}
                  onChange={handleChange}
                  min="1"
                  step="0.1"
                  className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                    errors.size ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="50"
                />
                {errors.size && <p className="text-red-500 text-sm mt-1">{errors.size}</p>}
              </div>
            </div>

            {/* Availability */}
            <div className="flex items-center">
              <input
                type="checkbox"
                id="is_available"
                name="is_available"
                checked={formData.is_available}
                onChange={handleChange}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label htmlFor="is_available" className="ml-2 block text-sm text-gray-700">
                Office is available for booking
              </label>
            </div>
          </div>

          {/* Footer */}
          <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex justify-end space-x-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
              disabled={isLoading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2 inline-block"></div>
                  {office ? 'Updating...' : 'Creating...'}
                </>
              ) : (
                office ? 'Update Office' : 'Create Office'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OfficeFormModal;