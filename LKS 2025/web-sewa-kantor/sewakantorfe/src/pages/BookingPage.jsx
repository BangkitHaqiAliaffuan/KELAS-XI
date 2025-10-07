import React, { useState, useEffect } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { 
  MapPin, 
  Calendar, 
  CreditCard, 
  User, 
  Mail, 
  Phone,
  CheckCircle,
  AlertCircle,
  ArrowLeft
} from 'lucide-react'
import api, { transactionService } from '../services/api'
import { useAuth } from '../context/AuthContext'

const BookingPage = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const { officeId } = useParams()
  const { user } = useAuth()
  
  const { office, startDate, endDate } = location.state || {}
  
  // If no office data, show error or redirect
  if (!office && !officeId) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-900 mb-2">No Office Selected</h2>
          <p className="text-gray-600 mb-4">Please select an office to book.</p>
          <button
            onClick={() => navigate('/offices')}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Browse Offices
          </button>
        </div>
      </div>
    )
  }
  
  const [formData, setFormData] = useState({
    name: user?.name || '',
    email: user?.email || '',
    phone: user?.phone || '',
    special_requests: ''
  })
  const [errors, setErrors] = useState({})
  const [step, setStep] = useState(1) // 1: Details, 2: Payment, 3: Confirmation
  const [isProcessingPayment, setIsProcessingPayment] = useState(false)

  const bookingMutation = useMutation({
    mutationFn: async (bookingData) => {
      const response = await api.post('/v1/bookings', bookingData);
      return response.data;
    },
    onSuccess: (data) => {
      setStep(3)
    },
    onError: (error) => {
      setErrors({ submit: error.message || 'Booking failed. Please try again.' })
    }
  })

  useEffect(() => {
    if (!office || !startDate || !endDate) {
      navigate('/offices')
    }
  }, [office, startDate, endDate, navigate])

  const calculateDays = () => {
    if (!startDate || !endDate) return 0
    const start = new Date(startDate)
    const end = new Date(endDate)
    const diffTime = Math.abs(end - start)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays || 1
  }

  const calculateTotal = () => {
    const days = calculateDays()
    // Use daily price from office data structure
    const dailyPrice = office?.data?.price_for_duration?.daily || office?.data?.price_per_day || 0
    const subtotal = parseFloat(dailyPrice) * days
    const serviceFee = subtotal * 0.05 // 5% service fee
    const tax = subtotal * 0.11 // 11% tax (PPN)
    return {
      subtotal,
      serviceFee,
      tax,
      total: subtotal + serviceFee + tax,
      dailyPrice: parseFloat(dailyPrice)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }
  }

  const validateForm = () => {
    const newErrors = {}

    if (!formData.name) {
      newErrors.name = 'Name is required'
    }

    if (!formData.email) {
      newErrors.email = 'Email is required'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email is invalid'
    }

    if (!formData.phone) {
      newErrors.phone = 'Phone number is required'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (step === 1) {
      if (!validateForm()) return
      setStep(2)
    } else if (step === 2) {
      // Simulate payment processing
      setErrors({})
      setIsProcessingPayment(true)
      
      // Dummy payment processing
      setTimeout(() => {
        // Process booking after "payment"
        const bookingData = {
          office_id: office?.data?.id || office?.id,
          start_date: startDate,
          end_date: endDate,
          rental_type: 'daily', // daily, weekly, monthly
          customer_name: formData.name,
          customer_email: formData.email,
          customer_phone: formData.phone,
          notes: formData.special_requests || '',
          payment_method: 'credit_card',
        }
        
        console.log('Booking data:', bookingData)
        
        // Submit the actual booking
        bookingMutation.mutate(bookingData, {
          onSuccess: () => {
            setIsProcessingPayment(false)
            setStep(3)
          },
          onError: (error) => {
            setIsProcessingPayment(false)
            console.error('Booking error:', error)
            alert('Failed to create booking: ' + (error.response?.data?.message || error.message))
          }
        })
      }, 2000) // 2 second delay to simulate processing
    }
  }

  if (!office) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Invalid Booking</h2>
          <p className="text-gray-600 mb-4">Please select an office to book.</p>
          <button 
            onClick={() => navigate('/offices')}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            Browse Offices
          </button>
        </div>
      </div>
    )
  }

  const costs = calculateTotal()
  const days = calculateDays()

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <button 
            onClick={() => navigate(-1)}
            className="mb-4 text-blue-600 hover:text-blue-800 flex items-center"
          >
            <ArrowLeft className="h-5 w-5 mr-2" />
            Back
          </button>
          <h1 className="text-3xl font-bold text-gray-900">
            {step === 1 && 'Booking Details'}
            {step === 2 && 'Payment'}
            {step === 3 && 'Booking Confirmed'}
          </h1>
        </div>

        {/* Progress Bar */}
        <div className="mb-8">
          <div className="flex items-center">
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
              step >= 1 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              1
            </div>
            <div className={`flex-1 h-1 mx-4 ${
              step >= 2 ? 'bg-blue-600' : 'bg-gray-300'
            }`} />
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
              step >= 2 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              2
            </div>
            <div className={`flex-1 h-1 mx-4 ${
              step >= 3 ? 'bg-blue-600' : 'bg-gray-300'
            }`} />
            <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
              step >= 3 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              3
            </div>
          </div>
          <div className="flex justify-between text-sm text-gray-600 mt-2">
            <span>Details</span>
            <span>Payment</span>
            <span>Confirmation</span>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column - Form */}
          <div className="lg:col-span-2">
            {step === 1 && (
              <div className="bg-white rounded-lg shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">Contact Information</h2>
                
                {errors.submit && (
                  <div className="mb-4 bg-red-50 border border-red-200 rounded-md p-4 flex items-center">
                    <AlertCircle className="h-5 w-5 text-red-400 mr-2" />
                    <span className="text-sm text-red-700">{errors.submit}</span>
                  </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                  <div>
                    <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                      Full Name *
                    </label>
                    <div className="mt-1 relative">
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <User className="h-5 w-5 text-gray-400" />
                      </div>
                      <input
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        className={`block w-full pl-10 pr-3 py-2 border ${
                          errors.name ? 'border-red-300' : 'border-gray-300'
                        } rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500`}
                        placeholder="Enter your full name"
                      />
                    </div>
                    {errors.name && (
                      <p className="mt-1 text-sm text-red-600">{errors.name}</p>
                    )}
                  </div>

                  <div>
                    <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                      Email Address *
                    </label>
                    <div className="mt-1 relative">
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Mail className="h-5 w-5 text-gray-400" />
                      </div>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        className={`block w-full pl-10 pr-3 py-2 border ${
                          errors.email ? 'border-red-300' : 'border-gray-300'
                        } rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500`}
                        placeholder="Enter your email address"
                      />
                    </div>
                    {errors.email && (
                      <p className="mt-1 text-sm text-red-600">{errors.email}</p>
                    )}
                  </div>

                  <div>
                    <label htmlFor="phone" className="block text-sm font-medium text-gray-700">
                      Phone Number *
                    </label>
                    <div className="mt-1 relative">
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Phone className="h-5 w-5 text-gray-400" />
                      </div>
                      <input
                        type="tel"
                        id="phone"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        className={`block w-full pl-10 pr-3 py-2 border ${
                          errors.phone ? 'border-red-300' : 'border-gray-300'
                        } rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500`}
                        placeholder="Enter your phone number"
                      />
                    </div>
                    {errors.phone && (
                      <p className="mt-1 text-sm text-red-600">{errors.phone}</p>
                    )}
                  </div>

                  <div>
                    <label htmlFor="special_requests" className="block text-sm font-medium text-gray-700">
                      Special Requests (Optional)
                    </label>
                    <div className="mt-1">
                      <textarea
                        id="special_requests"
                        name="special_requests"
                        rows={4}
                        value={formData.special_requests}
                        onChange={handleChange}
                        className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Any special requirements or requests..."
                      />
                    </div>
                  </div>

                  <div className="flex justify-end">
                    <button
                      type="submit"
                      className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    >
                      Continue to Payment
                    </button>
                  </div>
                </form>
              </div>
            )}

            {step === 2 && (
              <div className="bg-white rounded-lg shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">Payment Method</h2>
                
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
                    <div className="flex items-center">
                      <CreditCard className="h-5 w-5 text-blue-600 mr-2" />
                      <span className="font-medium text-blue-900">Credit/Debit Card</span>
                    </div>
                    <p className="text-sm text-blue-700 mt-1">
                      Payment will be processed securely through our payment gateway.
                    </p>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">
                        Card Number
                      </label>
                      <input
                        type="text"
                        placeholder="1234 5678 9012 3456"
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                      />
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Expiry Date
                        </label>
                        <input
                          type="text"
                          placeholder="MM/YY"
                          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          CVV
                        </label>
                        <input
                          type="text"
                          placeholder="123"
                          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700">
                        Cardholder Name
                      </label>
                      <input
                        type="text"
                        placeholder="John Doe"
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                      />
                    </div>
                  </div>

                  <div className="flex justify-between">
                    <button
                      type="button"
                      onClick={() => setStep(1)}
                      className="bg-gray-300 text-gray-700 px-6 py-2 rounded-md hover:bg-gray-400"
                    >
                      Back
                    </button>
                    <button
                      type="submit"
                      disabled={isProcessingPayment || bookingMutation.isPending}
                      className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center"
                    >
                      {(isProcessingPayment || bookingMutation.isPending) && (
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      )}
                      {isProcessingPayment ? 'Processing Payment...' : 
                       bookingMutation.isPending ? 'Completing Booking...' : 'Complete Booking'}
                    </button>
                  </div>
                </form>
              </div>
            )}

            {step === 3 && (
              <div className="bg-white rounded-lg shadow-lg p-6 text-center">
                <CheckCircle className="h-16 w-16 text-green-500 mx-auto mb-4" />
                <h2 className="text-2xl font-bold text-gray-900 mb-4">
                  Booking Confirmed!
                </h2>
                <p className="text-gray-600 mb-6">
                  Your office booking has been successfully confirmed. You will receive a confirmation email shortly.
                </p>
                <div className="flex justify-center space-x-4">
                  <button
                    onClick={() => navigate('/dashboard')}
                    className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700"
                  >
                    View My Bookings
                  </button>
                  <button
                    onClick={() => navigate('/offices')}
                    className="bg-gray-300 text-gray-700 px-6 py-2 rounded-md hover:bg-gray-400"
                  >
                    Browse More Offices
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Right Column - Booking Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-lg p-6 sticky top-8">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Booking Summary</h3>
              
              {/* Office Info */}
              <div className="mb-6">
                <h4 className="font-medium text-gray-900">{office?.data?.name || office?.name}</h4>
                <div className="flex items-center text-gray-600 text-sm mt-1">
                  <MapPin className="h-4 w-4 mr-1" />
                  <span>{office?.data?.address || office?.address}, {office?.data?.city?.name || office?.city?.name}</span>
                </div>
              </div>

              {/* Dates */}
              <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center text-gray-700 mb-2">
                  <Calendar className="h-4 w-4 mr-2" />
                  <span className="font-medium">Booking Period</span>
                </div>
                <div className="text-sm text-gray-600">
                  <p>Check-in: {new Date(startDate).toLocaleDateString('id-ID', { 
                    weekday: 'long', 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric' 
                  })}</p>
                  <p>Check-out: {new Date(endDate).toLocaleDateString('id-ID', { 
                    weekday: 'long', 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric' 
                  })}</p>
                  <p className="mt-2 font-medium">Duration: {days} day{days > 1 ? 's' : ''}</p>
                </div>
              </div>

              {/* Price Breakdown */}
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Price per day</span>
                  <span>Rp {costs.dailyPrice.toLocaleString('id-ID')}</span>
                </div>
                <div className="flex justify-between">
                  <span>Subtotal ({days} day{days > 1 ? 's' : ''})</span>
                  <span>Rp {costs.subtotal.toLocaleString('id-ID')}</span>
                </div>
                <div className="flex justify-between">
                  <span>Service fee (5%)</span>
                  <span>Rp {costs.serviceFee.toLocaleString('id-ID')}</span>
                </div>
                <div className="flex justify-between">
                  <span>Tax (11%)</span>
                  <span>Rp {costs.tax.toLocaleString('id-ID')}</span>
                </div>
                <div className="border-t border-gray-200 pt-2 mt-2">
                  <div className="flex justify-between font-bold text-lg">
                    <span>Total</span>
                    <span className="text-blue-600">Rp {costs.total.toLocaleString('id-ID')}</span>
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

export default BookingPage
