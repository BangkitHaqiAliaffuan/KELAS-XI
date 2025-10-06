import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { format } from 'date-fns'
import { id } from 'date-fns/locale'
import { 
  CalendarDays, 
  MapPin, 
  Clock, 
  DollarSign, 
  TrendingUp, 
  Users, 
  Building, 
  CheckCircle, 
  XCircle, 
  AlertCircle,
  Eye,
  X,
  Download,
  Filter
} from 'lucide-react'
import Layout from '../components/Layout'
import { dashboardService } from '../services/api'
import { useAuth } from '../context/AuthContext'

const DashboardPage = () => {
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const [activeTab, setActiveTab] = useState('overview')
  const [bookingFilter, setBookingFilter] = useState('')
  const [selectedBooking, setSelectedBooking] = useState(null)
  const [showBookingModal, setShowBookingModal] = useState(false)

  // Fetch dashboard statistics
  const { data: statistics, isLoading: statsLoading } = useQuery({
    queryKey: ['dashboard', 'statistics'],
    queryFn: dashboardService.getStatistics
  })

  // Fetch user bookings
  const { data: bookingsData, isLoading: bookingsLoading } = useQuery({
    queryKey: ['dashboard', 'bookings', bookingFilter],
    queryFn: () => dashboardService.getUserBookings({ 
      status: bookingFilter || undefined,
      per_page: 20 
    })
  })

  // Cancel booking mutation
  const cancelBookingMutation = useMutation({
    mutationFn: dashboardService.cancelBooking,
    onSuccess: () => {
      queryClient.invalidateQueries(['dashboard', 'bookings'])
      queryClient.invalidateQueries(['dashboard', 'statistics'])
    }
  })

  const stats = statistics?.data || {}
  const bookings = bookingsData?.data?.data || []

  const handleCancelBooking = async (bookingId) => {
    if (window.confirm('Are you sure you want to cancel this booking?')) {
      try {
        await cancelBookingMutation.mutateAsync(bookingId)
        alert('Booking cancelled successfully!')
      } catch (error) {
        alert('Failed to cancel booking: ' + (error.response?.data?.message || error.message))
      }
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'confirmed':
        return 'text-green-600 bg-green-100'
      case 'pending':
        return 'text-yellow-600 bg-yellow-100'
      case 'cancelled':
        return 'text-red-600 bg-red-100'
      case 'completed':
        return 'text-blue-600 bg-blue-100'
      default:
        return 'text-gray-600 bg-gray-100'
    }
  }

  const getStatusIcon = (status) => {
    switch (status) {
      case 'confirmed':
        return <CheckCircle className="h-4 w-4" />
      case 'cancelled':
        return <XCircle className="h-4 w-4" />
      default:
        return <AlertCircle className="h-4 w-4" />
    }
  }

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR'
    }).format(amount)
  }

  const StatCard = ({ title, value, icon: Icon, trend, trendValue, color = "blue" }) => (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-semibold text-gray-900">{value}</p>
          {trend && (
            <p className={`text-sm ${trend === 'up' ? 'text-green-600' : 'text-red-600'} flex items-center`}>
              <TrendingUp className="h-4 w-4 mr-1" />
              {trendValue}
            </p>
          )}
        </div>
        <div className={`p-3 rounded-full bg-${color}-100`}>
          <Icon className={`h-6 w-6 text-${color}-600`} />
        </div>
      </div>
    </div>
  )

  const BookingCard = ({ booking }) => (
    <div className="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">
            {booking.office?.name || 'Office Name'}
          </h3>
          <div className="flex items-center text-gray-600 mt-1">
            <MapPin className="h-4 w-4 mr-1" />
            <span className="text-sm">
              {booking.office?.city?.name || 'City'}, {booking.office?.address}
            </span>
          </div>
        </div>
        <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
          {getStatusIcon(booking.status)}
          <span className="ml-1 capitalize">{booking.status}</span>
        </div>
      </div>
      
      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="flex items-center text-gray-600">
          <CalendarDays className="h-4 w-4 mr-2" />
          <div>
            <p className="text-sm font-medium">Check-in</p>
            <p className="text-xs">
              {booking.start_date ? format(new Date(booking.start_date), 'dd MMM yyyy', { locale: id }) : '-'}
            </p>
          </div>
        </div>
        <div className="flex items-center text-gray-600">
          <Clock className="h-4 w-4 mr-2" />
          <div>
            <p className="text-sm font-medium">Check-out</p>
            <p className="text-xs">
              {booking.end_date ? format(new Date(booking.end_date), 'dd MMM yyyy', { locale: id }) : '-'}
            </p>
          </div>
        </div>
      </div>
      
      <div className="flex justify-between items-center pt-4 border-t">
        <div className="flex items-center text-gray-900">
          <DollarSign className="h-4 w-4 mr-1" />
          <span className="font-semibold">{formatCurrency(booking.total_price || 0)}</span>
          <span className="text-sm text-gray-600 ml-1">
            ({booking.days || 0} hari)
          </span>
        </div>
        <div className="flex space-x-2">
          <button
            onClick={() => {
              setSelectedBooking(booking)
              setShowBookingModal(true)
            }}
            className="inline-flex items-center px-3 py-1 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50"
          >
            <Eye className="h-3 w-3 mr-1" />
            Detail
          </button>
          {booking.status === 'confirmed' && new Date(booking.start_date) > new Date() && (
            <button
              onClick={() => handleCancelBooking(booking.id)}
              disabled={cancelBookingMutation.isPending}
              className="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-white bg-red-600 hover:bg-red-700 disabled:opacity-50"
            >
              <X className="h-3 w-3 mr-1" />
              {cancelBookingMutation.isPending ? 'Canceling...' : 'Cancel'}
            </button>
          )}
        </div>
      </div>
    </div>
  )

  const BookingModal = ({ booking, isOpen, onClose }) => {
    if (!isOpen || !booking) return null

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
          <div className="p-6">
            <div className="flex justify-between items-start mb-6">
              <h2 className="text-xl font-semibold text-gray-900">Booking Details</h2>
              <button
                onClick={onClose}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="h-6 w-6" />
              </button>
            </div>
            
            <div className="space-y-6">
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">Office Information</h3>
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="font-semibold">{booking.office?.name}</p>
                  <p className="text-gray-600">{booking.office?.address}</p>
                  <p className="text-gray-600">{booking.office?.city?.name}</p>
                </div>
              </div>
              
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">Booking Information</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm font-medium text-gray-600">Booking Code</p>
                    <p className="text-lg font-mono">{booking.booking_code || '-'}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600">Status</p>
                    <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
                      {getStatusIcon(booking.status)}
                      <span className="ml-1 capitalize">{booking.status}</span>
                    </div>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600">Check-in Date</p>
                    <p>{booking.start_date ? format(new Date(booking.start_date), 'dd MMMM yyyy', { locale: id }) : '-'}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600">Check-out Date</p>
                    <p>{booking.end_date ? format(new Date(booking.end_date), 'dd MMMM yyyy', { locale: id }) : '-'}</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600">Duration</p>
                    <p>{booking.days || 0} days</p>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-600">Total Price</p>
                    <p className="text-lg font-semibold text-green-600">{formatCurrency(booking.total_price || 0)}</p>
                  </div>
                </div>
              </div>

              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">Customer Information</h3>
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="font-semibold">{booking.customer_name || booking.name || user?.name}</p>
                  <p className="text-gray-600">{booking.customer_email || booking.email || user?.email}</p>
                  <p className="text-gray-600">{booking.customer_phone || booking.phone || user?.phone}</p>
                </div>
              </div>

              {booking.special_requests && (
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">Special Requests</h3>
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <p className="text-gray-700">{booking.special_requests}</p>
                  </div>
                </div>
              )}
            </div>
            
            <div className="flex justify-end space-x-3 mt-6 pt-6 border-t">
              <button
                onClick={onClose}
                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                Close
              </button>
              <button className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 flex items-center">
                <Download className="h-4 w-4 mr-2" />
                Download Receipt
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <Layout>
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">
              Welcome back, {user?.name || 'User'}!
            </h1>
            <p className="text-gray-600 mt-2">
              Manage your office bookings and track your rental history
            </p>
          </div>

          {/* Tabs */}
          <div className="mb-8">
            <nav className="flex space-x-8">
              <button
                onClick={() => setActiveTab('overview')}
                className={`py-2 px-1 border-b-2 font-medium text-sm ${
                  activeTab === 'overview'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Overview
              </button>
              <button
                onClick={() => setActiveTab('bookings')}
                className={`py-2 px-1 border-b-2 font-medium text-sm ${
                  activeTab === 'bookings'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                My Bookings
              </button>
            </nav>
          </div>

          {activeTab === 'overview' && (
            <div className="space-y-8">
              {/* Statistics Cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {statsLoading ? (
                  Array.from({ length: 4 }).map((_, i) => (
                    <div key={i} className="bg-white rounded-lg shadow p-6 animate-pulse">
                      <div className="h-4 bg-gray-200 rounded w-1/2 mb-2"></div>
                      <div className="h-8 bg-gray-200 rounded w-3/4"></div>
                    </div>
                  ))
                ) : (
                  <>
                    <StatCard
                      title="Total Bookings"
                      value={stats.total_bookings || 0}
                      icon={Building}
                      color="blue"
                    />
                    <StatCard
                      title="Upcoming Bookings"
                      value={stats.upcoming_bookings || 0}
                      icon={CalendarDays}
                      color="green"
                    />
                    <StatCard
                      title="Active Bookings"
                      value={stats.active_bookings || 0}
                      icon={Users}
                      color="yellow"
                    />
                    <StatCard
                      title="Total Spent"
                      value={formatCurrency(stats.total_spent || 0)}
                      icon={DollarSign}
                      color="purple"
                    />
                  </>
                )}
              </div>

              {/* Recent Bookings */}
              <div className="bg-white rounded-lg shadow">
                <div className="p-6 border-b">
                  <h2 className="text-lg font-semibold text-gray-900">Recent Bookings</h2>
                </div>
                <div className="p-6">
                  {bookingsLoading ? (
                    <div className="space-y-4">
                      {Array.from({ length: 3 }).map((_, i) => (
                        <div key={i} className="animate-pulse">
                          <div className="h-4 bg-gray-200 rounded w-1/4 mb-2"></div>
                          <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                        </div>
                      ))}
                    </div>
                  ) : bookings.length > 0 ? (
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                      {bookings.slice(0, 4).map((booking) => (
                        <BookingCard key={booking.id} booking={booking} />
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      <Building className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                      <p>No bookings found. Start by booking an office!</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'bookings' && (
            <div className="space-y-6">
              {/* Filter */}
              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex items-center space-x-4">
                  <Filter className="h-5 w-5 text-gray-400" />
                  <select
                    value={bookingFilter}
                    onChange={(e) => setBookingFilter(e.target.value)}
                    className="border border-gray-300 rounded-md px-3 py-2 bg-white"
                  >
                    <option value="">All Bookings</option>
                    <option value="confirmed">Confirmed</option>
                    <option value="pending">Pending</option>
                    <option value="completed">Completed</option>
                    <option value="cancelled">Cancelled</option>
                  </select>
                </div>
              </div>

              {/* Bookings List */}
              <div className="bg-white rounded-lg shadow">
                <div className="p-6 border-b">
                  <h2 className="text-lg font-semibold text-gray-900">
                    My Bookings {bookingFilter && `(${bookingFilter})`}
                  </h2>
                </div>
                <div className="p-6">
                  {bookingsLoading ? (
                    <div className="space-y-4">
                      {Array.from({ length: 5 }).map((_, i) => (
                        <div key={i} className="animate-pulse border rounded-lg p-4">
                          <div className="h-4 bg-gray-200 rounded w-1/3 mb-2"></div>
                          <div className="h-3 bg-gray-200 rounded w-1/2 mb-2"></div>
                          <div className="h-3 bg-gray-200 rounded w-1/4"></div>
                        </div>
                      ))}
                    </div>
                  ) : bookings.length > 0 ? (
                    <div className="space-y-6">
                      {bookings.map((booking) => (
                        <BookingCard key={booking.id} booking={booking} />
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-12 text-gray-500">
                      <Building className="h-16 w-16 mx-auto mb-4 text-gray-300" />
                      <p className="text-lg font-medium mb-2">No bookings found</p>
                      <p>
                        {bookingFilter 
                          ? `No ${bookingFilter} bookings available.`
                          : 'You haven\'t made any bookings yet.'
                        }
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Booking Detail Modal */}
      <BookingModal
        booking={selectedBooking}
        isOpen={showBookingModal}
        onClose={() => {
          setShowBookingModal(false)
          setSelectedBooking(null)
        }}
      />
    </Layout>
  )
}

export default DashboardPage
