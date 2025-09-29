import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { 
  MapPin, 
  Users, 
  Wifi, 
  Car, 
  Coffee, 
  Shield, 
  Clock,
  Star,
  Calendar,
  CreditCard
} from 'lucide-react'
import { officeService } from '../services/api'
import { useAuth } from '../context/AuthContext'

const OfficeDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const [selectedStartDate, setSelectedStartDate] = useState('')
  const [selectedEndDate, setSelectedEndDate] = useState('')
  const [selectedImageIndex, setSelectedImageIndex] = useState(0)
  const [selectedDuration, setSelectedDuration] = useState('daily')

  const { data: office, isLoading, error } = useQuery({
    queryKey: ['office', id],
    queryFn: () => officeService.getById(id)
  })

  const handleBooking = () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/offices/${id}` } })
      return
    }

    if (!selectedStartDate || !selectedEndDate) {
      alert('Please select both start and end dates')
      return
    }

    // Navigate to booking page with office and date data
    navigate('/booking', { 
      state: { 
        office, 
        startDate: selectedStartDate, 
        endDate: selectedEndDate 
      } 
    })
  }

  const calculateDays = () => {
    if (!selectedStartDate || !selectedEndDate) return 0
    const start = new Date(selectedStartDate)
    const end = new Date(selectedEndDate)
    const diffTime = Math.abs(end - start)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays || 1
  }

  const calculateTotal = () => {
    if (!office?.price_for_duration) return 0
    
    const days = calculateDays()
    const prices = office.price_for_duration
    
    if (selectedDuration === 'daily') {
      return days * parseFloat(prices.daily || 0)
    } else if (selectedDuration === 'weekly') {
      const weeks = Math.ceil(days / 7)
      return weeks * parseFloat(prices.weekly || 0)
    } else if (selectedDuration === 'monthly') {
      const months = Math.ceil(days / 30)
      return months * parseFloat(prices.monthly || 0)
    }
    
    return days * parseFloat(prices.daily || 0)
  }

  const getCurrentPrice = () => {
    if (!office?.price_for_duration) return 0
    const prices = office.price_for_duration
    return parseFloat(prices[selectedDuration] || prices.daily || 0)
  }

  const getDurationText = () => {
    switch (selectedDuration) {
      case 'weekly': return 'per week'
      case 'monthly': return 'per month'
      default: return 'per day'
    }
  }

  const getBillingUnits = () => {
    const days = calculateDays()
    if (selectedDuration === 'weekly') {
      return Math.ceil(days / 7)
    } else if (selectedDuration === 'monthly') {
      return Math.ceil(days / 30)
    }
    return days
  }

  const getBillingUnitText = () => {
    const units = getBillingUnits()
    switch (selectedDuration) {
      case 'weekly': 
        return `${units} week${units > 1 ? 's' : ''}`
      case 'monthly': 
        return `${units} month${units > 1 ? 's' : ''}`
      default: 
        return `${units} day${units > 1 ? 's' : ''}`
    }
  }

  const facilityIcons = {
    wifi: Wifi,
    parking: Car,
    coffee: Coffee,
    security: Shield,
    meeting_room: Users
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (error || !office) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Office Not Found</h2>
          <p className="text-gray-600 mb-4">The office you're looking for doesn't exist.</p>
          <button 
            onClick={() => navigate('/offices')}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            Back to Offices
          </button>
        </div>
      </div>
    )
  }

  // Mock images for demo - in real app, these would come from the API
  const images = [
    'https://images.unsplash.com/photo-1497366216548-37526070297c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1497366754035-f200968a6e72?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    'https://images.unsplash.com/photo-1542744173-8e7e53415bb0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'
  ]

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Back Button */}
        <button 
          onClick={() => navigate('/offices')}
          className="mb-6 text-blue-600 hover:text-blue-800 flex items-center"
        >
          ← Back to Offices
        </button>

        <div className="bg-white rounded-lg shadow-lg overflow-hidden">
          {/* Image Gallery */}
          <div className="relative h-96 bg-gray-200">
            <img 
              src={images[selectedImageIndex]} 
              alt={office.name}
              className="w-full h-full object-cover"
            />
            <div className="absolute bottom-4 left-4 flex space-x-2">
              {images.map((_, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImageIndex(index)}
                  className={`w-3 h-3 rounded-full ${
                    selectedImageIndex === index ? 'bg-white' : 'bg-white/60'
                  }`}
                />
              ))}
            </div>
          </div>

          <div className="p-8">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Left Column - Office Details */}
              <div className="lg:col-span-2">
                <div className="mb-6">
                  <h1 className="text-3xl font-bold text-gray-900 mb-2">{office.name}</h1>
                  <div className="flex items-center text-gray-600 mb-4">
                    <MapPin className="h-5 w-5 mr-2" />
                    <span>{office.address}, {office.city?.name}</span>
                  </div>
                  <div className="flex items-center mb-4">
                    <div className="flex items-center">
                      {[...Array(5)].map((_, i) => (
                        <Star 
                          key={i} 
                          className={`h-5 w-5 ${i < 4 ? 'text-yellow-400 fill-current' : 'text-gray-300'}`} 
                        />
                      ))}
                      <span className="ml-2 text-gray-600">(4.2) • 23 reviews</span>
                    </div>
                  </div>
                </div>

                {/* Description */}
                <div className="mb-8">
                  <h3 className="text-xl font-semibold text-gray-900 mb-3">Description</h3>
                  <p className="text-gray-600 leading-relaxed">
                    {office.description || "This modern office space offers a professional environment perfect for businesses of all sizes. Located in the heart of the business district, it provides easy access to public transportation and various amenities. The space is fully furnished and ready to move in."}
                  </p>
                </div>

                {/* Facilities */}
                <div className="mb-8">
                  <h3 className="text-xl font-semibold text-gray-900 mb-4">Facilities</h3>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    {office.facilities?.map((facility) => {
                      const IconComponent = facilityIcons[facility.icon] || Shield
                      return (
                        <div key={facility.id} className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <IconComponent className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">{facility.name}</span>
                        </div>
                      )
                    }) || (
                      <>
                        <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <Wifi className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">High Speed WiFi</span>
                        </div>
                        <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <Car className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">Parking</span>
                        </div>
                        <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <Coffee className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">Coffee & Tea</span>
                        </div>
                        <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <Shield className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">24/7 Security</span>
                        </div>
                        <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <Users className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">Meeting Rooms</span>
                        </div>
                        <div className="flex items-center p-3 bg-gray-50 rounded-lg">
                          <Clock className="h-5 w-5 text-blue-600 mr-3" />
                          <span className="text-gray-700">24/7 Access</span>
                        </div>
                      </>
                    )}
                  </div>
                </div>

                {/* Pricing Information */}
                {office.price_for_duration && (
                  <div className="mb-8">
                    <h3 className="text-xl font-semibold text-gray-900 mb-4">Pricing Options</h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      {office.price_for_duration.daily && (
                        <div className="p-4 bg-blue-50 rounded-lg border border-blue-200">
                          <p className="text-sm text-blue-600 font-medium">Daily Rate</p>
                          <p className="text-2xl font-bold text-blue-900">
                            Rp {parseFloat(office.price_for_duration.daily).toLocaleString('id-ID')}
                          </p>
                          <p className="text-sm text-blue-600">per day</p>
                        </div>
                      )}
                      {office.price_for_duration.weekly && (
                        <div className="p-4 bg-green-50 rounded-lg border border-green-200">
                          <p className="text-sm text-green-600 font-medium">Weekly Rate</p>
                          <p className="text-2xl font-bold text-green-900">
                            Rp {parseFloat(office.price_for_duration.weekly).toLocaleString('id-ID')}
                          </p>
                          <p className="text-sm text-green-600">per week</p>
                          {office.price_for_duration.daily && (
                            <p className="text-xs text-green-500 mt-1">
                              Save {Math.round((1 - (parseFloat(office.price_for_duration.weekly) / 7) / parseFloat(office.price_for_duration.daily)) * 100)}%
                            </p>
                          )}
                        </div>
                      )}
                      {office.price_for_duration.monthly && (
                        <div className="p-4 bg-purple-50 rounded-lg border border-purple-200">
                          <p className="text-sm text-purple-600 font-medium">Monthly Rate</p>
                          <p className="text-2xl font-bold text-purple-900">
                            Rp {parseFloat(office.price_for_duration.monthly).toLocaleString('id-ID')}
                          </p>
                          <p className="text-sm text-purple-600">per month</p>
                          {office.price_for_duration.daily && (
                            <p className="text-xs text-purple-500 mt-1">
                              Save {Math.round((1 - (parseFloat(office.price_for_duration.monthly) / 30) / parseFloat(office.price_for_duration.daily)) * 100)}%
                            </p>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Office Details */}
                <div className="mb-8">
                  <h3 className="text-xl font-semibold text-gray-900 mb-4">Office Details</h3>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <p className="text-sm text-gray-600">Capacity</p>
                      <p className="text-lg font-semibold text-gray-900">
                        {office.capacity || '10-20'} People
                      </p>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <p className="text-sm text-gray-600">Size</p>
                      <p className="text-lg font-semibold text-gray-900">
                        {office.size || '250'} m²
                      </p>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <p className="text-sm text-gray-600">Floor</p>
                      <p className="text-lg font-semibold text-gray-900">
                        {office.floor || '5th'} Floor
                      </p>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <p className="text-sm text-gray-600">Available</p>
                      <p className="text-lg font-semibold text-green-600">
                        {office.is_available ? 'Yes' : 'No'}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Right Column - Booking Card */}
              <div className="lg:col-span-1">
                <div className="sticky top-8">
                  <div className="bg-white border border-gray-200 rounded-lg p-6 shadow-lg">
                    <div className="mb-6">
                      <div className="flex items-center justify-between mb-4">
                        <span className="text-3xl font-bold text-gray-900">
                          Rp {getCurrentPrice().toLocaleString('id-ID')}
                        </span>
                        <span className="text-gray-600">{getDurationText()}</span>
                      </div>
                      
                      {/* Duration Selection */}
                      {office.price_for_duration && (
                        <div className="mb-4">
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            Billing Period
                          </label>
                          <div className="grid grid-cols-3 gap-2">
                            {office.price_for_duration.daily && (
                              <button
                                onClick={() => setSelectedDuration('daily')}
                                className={`px-3 py-2 text-sm rounded-md border transition-colors ${
                                  selectedDuration === 'daily'
                                    ? 'bg-blue-600 text-white border-blue-600'
                                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                                }`}
                              >
                                Daily
                              </button>
                            )}
                            {office.price_for_duration.weekly && (
                              <button
                                onClick={() => setSelectedDuration('weekly')}
                                className={`px-3 py-2 text-sm rounded-md border transition-colors ${
                                  selectedDuration === 'weekly'
                                    ? 'bg-blue-600 text-white border-blue-600'
                                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                                }`}
                              >
                                Weekly
                              </button>
                            )}
                            {office.price_for_duration.monthly && (
                              <button
                                onClick={() => setSelectedDuration('monthly')}
                                className={`px-3 py-2 text-sm rounded-md border transition-colors ${
                                  selectedDuration === 'monthly'
                                    ? 'bg-blue-600 text-white border-blue-600'
                                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                                }`}
                              >
                                Monthly
                              </button>
                            )}
                          </div>
                        </div>
                      )}
                    </div>

                    {/* Date Selection */}
                    <div className="mb-6">
                      <div className="grid grid-cols-1 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            <Calendar className="inline h-4 w-4 mr-1" />
                            Start Date
                          </label>
                          <input
                            type="date"
                            value={selectedStartDate}
                            onChange={(e) => setSelectedStartDate(e.target.value)}
                            min={new Date().toISOString().split('T')[0]}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            <Calendar className="inline h-4 w-4 mr-1" />
                            End Date
                          </label>
                          <input
                            type="date"
                            value={selectedEndDate}
                            onChange={(e) => setSelectedEndDate(e.target.value)}
                            min={selectedStartDate || new Date().toISOString().split('T')[0]}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Booking Summary */}
                    {selectedStartDate && selectedEndDate && (
                      <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                        <div className="flex justify-between items-center mb-2">
                          <span className="text-gray-600">Duration</span>
                          <span className="font-medium">{calculateDays()} days</span>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                          <span className="text-gray-600">Billing units</span>
                          <span className="font-medium">{getBillingUnitText()}</span>
                        </div>
                        <div className="flex justify-between items-center mb-2">
                          <span className="text-gray-600">Price {getDurationText()}</span>
                          <span className="font-medium">Rp {getCurrentPrice().toLocaleString('id-ID')}</span>
                        </div>
                        <div className="border-t border-gray-200 pt-2 mt-2">
                          <div className="flex justify-between items-center">
                            <span className="font-semibold">Total</span>
                            <span className="font-bold text-lg text-blue-600">
                              Rp {calculateTotal().toLocaleString('id-ID')}
                            </span>
                          </div>
                        </div>
                      </div>
                    )}

                    {/* Book Button */}
                    <button
                      onClick={handleBooking}
                      disabled={!office.is_available}
                      className={`w-full py-3 px-4 rounded-md font-semibold flex items-center justify-center ${
                        office.is_available
                          ? 'bg-blue-600 text-white hover:bg-blue-700'
                          : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                      }`}
                    >
                      <CreditCard className="h-5 w-5 mr-2" />
                      {office.is_available ? 'Book Now' : 'Not Available'}
                    </button>

                    {!isAuthenticated && (
                      <p className="text-sm text-gray-600 text-center mt-3">
                        Please login to make a booking
                      </p>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default OfficeDetailPage
