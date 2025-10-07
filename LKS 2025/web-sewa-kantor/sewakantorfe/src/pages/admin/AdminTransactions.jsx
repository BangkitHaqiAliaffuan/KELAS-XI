import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { 
  Receipt, 
  Search, 
  Filter,
  Eye,
  Download,
  Calendar,
  CreditCard,
  User,
  Building2,
  CheckCircle,
  AlertCircle,
  Clock,
  XCircle,
  Plus,
  Edit,
  Trash2
} from 'lucide-react'
import { transactionService } from '../../services/api'

const AdminTransactions = () => {
  const queryClient = useQueryClient()
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [paymentStatusFilter, setPaymentStatusFilter] = useState('')
  const [dateFilter, setDateFilter] = useState('')
  const [selectedTransaction, setSelectedTransaction] = useState(null)
  const [showDetailModal, setShowDetailModal] = useState(false)
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [deletingTransaction, setDeletingTransaction] = useState(null)
  const [notification, setNotification] = useState({ show: false, message: '', type: '' })

  const { data: transactions, isLoading, error } = useQuery({
    queryKey: ['transactions', searchTerm, statusFilter, paymentStatusFilter, dateFilter],
    queryFn: async () => {
      try {
        const params = {};
        
        // Only add non-empty parameters
        if (searchTerm && searchTerm.trim() !== '') {
          params.search = searchTerm.trim();
        }
        
        if (statusFilter && statusFilter !== '') {
          params.status = statusFilter;
        }
        
        if (paymentStatusFilter && paymentStatusFilter !== '') {
          params.payment_status = paymentStatusFilter;
        }
        
        if (dateFilter && dateFilter !== '') {
          // Handle different date filter types
          const today = new Date().toISOString().split('T')[0];
          
          if (dateFilter === 'today') {
            params.start_date = today;
            params.end_date = today;
          } else if (dateFilter === 'this_week') {
            const weekStart = new Date();
            weekStart.setDate(weekStart.getDate() - weekStart.getDay());
            params.start_date = weekStart.toISOString().split('T')[0];
            params.end_date = today;
          } else if (dateFilter === 'this_month') {
            const monthStart = new Date();
            monthStart.setDate(1);
            params.start_date = monthStart.toISOString().split('T')[0];
            params.end_date = today;
          } else {
            // If it's a specific date
            params.start_date = dateFilter;
          }
        }
        
        const result = await transactionService.getAll(params);
        
        // Debug: Check final_amount values
        if (result?.data) {
          const amounts = result.data.map(t => ({ 
            id: t.id, 
            final_amount: t.final_amount, 
            total_amount: t.total_amount,
            payment_status: t.payment_status 
          }));
          console.log('Transaction amounts:', amounts);
        }
        
        return result;
      } catch (error) {
        console.error('Transaction fetch error:', error);
        throw error;
      }
    },
    retry: 3,
    retryDelay: attemptIndex => Math.min(1000 * 2 ** attemptIndex, 5000),
    refetchOnWindowFocus: false
  })

  // Update Status Mutation
  const updateStatusMutation = useMutation({
    mutationFn: ({ id, status }) => transactionService.updateStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries(['transactions'])
      showNotification('Transaction status updated successfully!', 'success')
    },
    onError: (error) => {
      showNotification(
        error.response?.data?.message || 'Failed to update transaction status', 
        'error'
      )
    }
  })

  // Delete Transaction Mutation
  const deleteMutation = useMutation({
    mutationFn: transactionService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['transactions'])
      showNotification('Transaction deleted successfully!', 'success')
    },
    onError: (error) => {
      showNotification(
        error.response?.data?.message || 'Failed to delete transaction', 
        'error'
      )
    }
  })

  const showNotification = (message, type) => {
    setNotification({ show: true, message, type })
    setTimeout(() => {
      setNotification({ show: false, message: '', type: '' })
    }, 5000)
  }

  const handleStatusUpdate = (id, status) => {
    updateStatusMutation.mutate({ id, status })
  }

  const handleViewDetail = (transaction) => {
    setSelectedTransaction(transaction)
    setShowDetailModal(true)
  }

  const handleDelete = (transaction) => {
    setDeletingTransaction(transaction)
    setShowDeleteModal(true)
  }

  const confirmDelete = () => {
    if (deletingTransaction) {
      deleteMutation.mutate(deletingTransaction.id)
      setShowDeleteModal(false)
      setDeletingTransaction(null)
    }
  }

  const cancelDelete = () => {
    setShowDeleteModal(false)
    setDeletingTransaction(null)
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'completed':
        return 'bg-green-100 text-green-800'
      case 'confirmed':
        return 'bg-blue-100 text-blue-800'
      case 'cancelled':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const getPaymentStatusColor = (status) => {
    switch (status) {
      case 'paid':
        return 'bg-green-100 text-green-800'
      case 'pending':
        return 'bg-yellow-100 text-yellow-800'
      case 'failed':
        return 'bg-red-100 text-red-800'
      case 'cancelled':
        return 'bg-gray-100 text-gray-800'
      case 'refunded':
        return 'bg-purple-100 text-purple-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const getStatusIcon = (status) => {
    switch (status) {
      case 'completed':
        return <CheckCircle className="h-4 w-4" />
      case 'confirmed':
        return <Clock className="h-4 w-4" />
      case 'cancelled':
        return <XCircle className="h-4 w-4" />
      default:
        return <AlertCircle className="h-4 w-4" />
    }
  }

  if (isLoading) {
    return (
      <div className="p-6">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-300 rounded mb-4"></div>
          <div className="space-y-4">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="h-20 bg-gray-300 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <div className="flex">
            <AlertCircle className="h-5 w-5 text-red-400" />
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">Error loading transactions</h3>
            </div>
          </div>
          <div className="text-sm text-red-700 mt-2 space-y-1">
            <p>{error.response?.data?.message || error.message || 'Failed to load transactions. Please try again.'}</p>
            {error.response?.status && (
              <p className="text-xs">Status: {error.response.status}</p>
            )}
            {error.response?.data?.errors && (
              <pre className="text-xs bg-red-100 p-2 rounded mt-2 overflow-auto">
                {JSON.stringify(error.response.data.errors, null, 2)}
              </pre>
            )}
          </div>
          <div className="flex space-x-2 mt-3">
            <button
              onClick={() => queryClient.invalidateQueries(['transactions'])}
              className="text-sm bg-red-100 text-red-800 px-3 py-1 rounded hover:bg-red-200"
            >
              Retry
            </button>
            <button
              onClick={() => {
                // Clear all filters and retry
                setSearchTerm('');
                setStatusFilter('');
                setPaymentStatusFilter('');
                setDateFilter('');
                setTimeout(() => queryClient.invalidateQueries(['transactions']), 100);
              }}
              className="text-sm bg-gray-100 text-gray-800 px-3 py-1 rounded hover:bg-gray-200"
            >
              Clear Filters & Retry
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="p-6">
      {/* Notification */}
      {notification.show && (
        <div className={`fixed top-4 right-4 z-50 flex items-center p-4 rounded-md shadow-lg ${
          notification.type === 'success' 
            ? 'bg-green-50 border border-green-200' 
            : 'bg-red-50 border border-red-200'
        }`}>
          {notification.type === 'success' ? (
            <CheckCircle className="h-5 w-5 text-green-400 mr-3" />
          ) : (
            <AlertCircle className="h-5 w-5 text-red-400 mr-3" />
          )}
          <span className={`text-sm ${
            notification.type === 'success' ? 'text-green-700' : 'text-red-700'
          }`}>
            {notification.message}
          </span>
        </div>
      )}

      <div className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Transactions</h1>
            <p className="text-gray-600">Manage all booking transactions and payments</p>
          </div>
          <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 flex items-center">
            <Download className="h-5 w-5 mr-2" />
            Export
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow mb-6 p-6">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              type="text"
              placeholder="Search transactions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Status</option>
            <option value="confirmed">Confirmed</option>
            <option value="completed">Completed</option>
            <option value="cancelled">Cancelled</option>
          </select>

          <select
            value={paymentStatusFilter}
            onChange={(e) => setPaymentStatusFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Payment Status</option>
            <option value="pending">Pending</option>
            <option value="paid">Paid</option>
            <option value="failed">Failed</option>
            <option value="cancelled">Cancelled</option>
            <option value="refunded">Refunded</option>
          </select>

          <input
            type="date"
            value={dateFilter}
            onChange={(e) => setDateFilter(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          <div className="flex items-center space-x-2">
            <Filter className="h-5 w-5 text-gray-400" />
            <span className="text-sm text-gray-600">
              {transactions?.data?.length || 0} transactions
            </span>
          </div>
        </div>
      </div>

      {/* Transactions Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Transaction
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Customer
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Office
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Booking Period
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Amount
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Payment
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {transactions?.data?.map((transaction) => (
                <tr key={transaction.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="bg-blue-100 rounded-full p-2 mr-3">
                        <Receipt className="h-4 w-4 text-blue-600" />
                      </div>
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {transaction.booking_code || `#${transaction.id}`}
                        </div>
                        <div className="text-sm text-gray-500">
                          {new Date(transaction.created_at).toLocaleDateString('id-ID')}
                        </div>
                      </div>
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="bg-gray-100 rounded-full p-2 mr-3">
                        <User className="h-4 w-4 text-gray-600" />
                      </div>
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {transaction.customer_name || transaction.user?.name || 'N/A'}
                        </div>
                        <div className="text-sm text-gray-500">
                          {transaction.customer_email || transaction.user?.email || 'N/A'}
                        </div>
                        {transaction.customer_phone && (
                          <div className="text-xs text-gray-400">
                            {transaction.customer_phone}
                          </div>
                        )}
                      </div>
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">
                      {transaction.office?.name || 'N/A'}
                    </div>
                    <div className="text-sm text-gray-500">
                      {transaction.office?.address || 'N/A'}
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center text-sm text-gray-900">
                      <Calendar className="h-4 w-4 text-gray-400 mr-1" />
                      <div>
                        <div>{new Date(transaction.start_date).toLocaleDateString('id-ID')}</div>
                        <div className="text-gray-500">to {new Date(transaction.end_date).toLocaleDateString('id-ID')}</div>
                        <div className="text-xs text-blue-600 font-medium">
                          {transaction.duration_days} days • {transaction.rental_type}
                        </div>
                      </div>
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <CreditCard className="h-4 w-4 text-gray-400 mr-2" />
                      <div>
                        <div className="text-sm font-semibold text-gray-900">
                          Rp {transaction.final_amount?.toLocaleString('id-ID') || transaction.total_amount?.toLocaleString('id-ID')}
                        </div>
                        {transaction.tax_amount > 0 && (
                          <div className="text-xs text-gray-500">
                            +Rp {transaction.tax_amount?.toLocaleString('id-ID')} tax
                          </div>
                        )}
                      </div>
                    </div>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <select
                      value={transaction.status}
                      onChange={(e) => handleStatusUpdate(transaction.id, e.target.value)}
                      className={`text-xs font-semibold px-3 py-1 rounded-full border-none focus:outline-none cursor-pointer ${getStatusColor(transaction.status)}`}
                      disabled={updateStatusMutation.isLoading}
                    >
                      <option value="confirmed">Confirmed</option>
                      <option value="completed">Completed</option>
                      <option value="cancelled">Cancelled</option>
                    </select>
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${getPaymentStatusColor(transaction.payment_status)}`}>
                      {transaction.payment_status || 'pending'}
                    </div>
                    {transaction.payment_method && (
                      <div className="text-xs text-gray-500 mt-1">
                        via {transaction.payment_method}
                      </div>
                    )}
                  </td>
                  
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex items-center space-x-2">
                      <button 
                        onClick={() => handleViewDetail(transaction)}
                        className="text-blue-600 hover:text-blue-900 p-1 rounded-md hover:bg-blue-50"
                        title="View Details"
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                      <button 
                        className="text-green-600 hover:text-green-900 p-1 rounded-md hover:bg-green-50"
                        title="Download Invoice"
                      >
                        <Download className="h-4 w-4" />
                      </button>
                      <button 
                        onClick={() => handleDelete(transaction)}
                        className="text-red-600 hover:text-red-900 p-1 rounded-md hover:bg-red-50"
                        title="Delete Transaction"
                        disabled={deleteMutation.isLoading}
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {transactions?.data?.length === 0 && (
        <div className="text-center py-12">
          <Receipt className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No transactions found</h3>
          <p className="text-gray-600">Transactions will appear here once customers start booking.</p>
        </div>
      )}

      {/* Summary Cards */}
      <div className="mt-8 grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="bg-green-100 rounded-md p-3 mr-4">
              <CreditCard className="h-6 w-6 text-green-600" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600">Total Revenue</p>
              <p className="text-2xl font-bold text-gray-900">
                {(() => {
                  const paidTransactions = transactions?.data?.filter(t => t.payment_status === 'paid') || [];
                  const total = paidTransactions.reduce((sum, transaction) => {
                    // Convert to number and ensure it's valid
                    const amount = Number(transaction.final_amount);
                    return sum + (isNaN(amount) ? 0 : amount);
                  }, 0);
                  
                  return `Rp ${total.toLocaleString('id-ID')}`;
                })()}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="bg-blue-100 rounded-md p-3 mr-4">
              <CheckCircle className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600">Completed</p>
              <p className="text-2xl font-bold text-gray-900">
                {transactions?.data?.filter(t => t.status === 'completed').length || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="bg-yellow-100 rounded-md p-3 mr-4">
              <Clock className="h-6 w-6 text-yellow-600" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600">Confirmed</p>
              <p className="text-2xl font-bold text-gray-900">
                {transactions?.data?.filter(t => t.status === 'confirmed').length || 0}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="bg-purple-100 rounded-md p-3 mr-4">
              <Receipt className="h-6 w-6 text-purple-600" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600">Paid Transactions</p>
              <p className="text-2xl font-bold text-gray-900">
                {transactions?.data?.filter(t => t.payment_status === 'paid').length || 0}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Transaction Detail Modal */}
      {showDetailModal && selectedTransaction && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
              <h3 className="text-lg font-medium text-gray-900">Transaction Details</h3>
              <button
                onClick={() => setShowDetailModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <XCircle className="h-6 w-6" />
              </button>
            </div>
            
            <div className="p-6 max-h-[calc(90vh-120px)] overflow-y-auto">
              <div className="space-y-6">
                {/* Basic Info */}
                <div>
                  <h4 className="text-md font-semibold text-gray-900 mb-3">Basic Information</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-gray-600">Booking Code</p>
                      <p className="text-sm font-medium">{selectedTransaction.booking_code}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Created Date</p>
                      <p className="text-sm font-medium">{new Date(selectedTransaction.created_at).toLocaleString('id-ID')}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Duration</p>
                      <p className="text-sm font-medium">{selectedTransaction.duration_days} days ({selectedTransaction.rental_type})</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Status</p>
                      <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(selectedTransaction.status)}`}>
                        {selectedTransaction.status}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Customer Info */}
                <div>
                  <h4 className="text-md font-semibold text-gray-900 mb-3">Customer Information</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-gray-600">Name</p>
                      <p className="text-sm font-medium">{selectedTransaction.customer_name}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Email</p>
                      <p className="text-sm font-medium">{selectedTransaction.customer_email}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Phone</p>
                      <p className="text-sm font-medium">{selectedTransaction.customer_phone || 'N/A'}</p>
                    </div>
                  </div>
                </div>

                {/* Office Info */}
                <div>
                  <h4 className="text-md font-semibold text-gray-900 mb-3">Office Information</h4>
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex items-start">
                      <Building2 className="h-5 w-5 text-gray-400 mt-1 mr-3" />
                      <div>
                        <p className="font-medium text-gray-900">{selectedTransaction.office?.name}</p>
                        <p className="text-sm text-gray-600">{selectedTransaction.office?.address}</p>
                        <p className="text-sm text-gray-500">{selectedTransaction.office?.city?.name}</p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Booking Period */}
                <div>
                  <h4 className="text-md font-semibold text-gray-900 mb-3">Booking Period</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-gray-600">Start Date</p>
                      <p className="text-sm font-medium">{new Date(selectedTransaction.start_date).toLocaleDateString('id-ID')}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">End Date</p>
                      <p className="text-sm font-medium">{new Date(selectedTransaction.end_date).toLocaleDateString('id-ID')}</p>
                    </div>
                  </div>
                </div>

                {/* Payment Info */}
                <div>
                  <h4 className="text-md font-semibold text-gray-900 mb-3">Payment Information</h4>
                  <div className="bg-gray-50 rounded-lg p-4 space-y-3">
                    <div className="flex justify-between">
                      <span className="text-sm text-gray-600">Subtotal</span>
                      <span className="text-sm font-medium">Rp {selectedTransaction.total_amount?.toLocaleString('id-ID')}</span>
                    </div>
                    {selectedTransaction.tax_amount > 0 && (
                      <div className="flex justify-between">
                        <span className="text-sm text-gray-600">Tax (11%)</span>
                        <span className="text-sm font-medium">Rp {selectedTransaction.tax_amount?.toLocaleString('id-ID')}</span>
                      </div>
                    )}
                    {selectedTransaction.discount_amount > 0 && (
                      <div className="flex justify-between">
                        <span className="text-sm text-gray-600">Discount</span>
                        <span className="text-sm font-medium text-red-600">-Rp {selectedTransaction.discount_amount?.toLocaleString('id-ID')}</span>
                      </div>
                    )}
                    <div className="border-t pt-2">
                      <div className="flex justify-between">
                        <span className="text-base font-semibold text-gray-900">Total</span>
                        <span className="text-base font-bold text-gray-900">
                          Rp {selectedTransaction.final_amount?.toLocaleString('id-ID')}
                        </span>
                      </div>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Payment Status</span>
                      <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getPaymentStatusColor(selectedTransaction.payment_status)}`}>
                        {selectedTransaction.payment_status || 'pending'}
                      </span>
                    </div>
                    {selectedTransaction.payment_method && (
                      <div className="flex justify-between">
                        <span className="text-sm text-gray-600">Payment Method</span>
                        <span className="text-sm font-medium">{selectedTransaction.payment_method}</span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Notes */}
                {selectedTransaction.notes && (
                  <div>
                    <h4 className="text-md font-semibold text-gray-900 mb-3">Notes</h4>
                    <div className="bg-gray-50 rounded-lg p-4">
                      <p className="text-sm text-gray-700">{selectedTransaction.notes}</p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteModal && deletingTransaction && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full">
            <div className="px-6 py-4">
              <div className="flex items-center mb-4">
                <div className="bg-red-100 rounded-full p-2 mr-3">
                  <AlertCircle className="h-6 w-6 text-red-600" />
                </div>
                <h3 className="text-lg font-semibold text-gray-900">Delete Transaction</h3>
              </div>
              
              <p className="text-gray-600 mb-6">
                Are you sure you want to delete transaction <strong>{deletingTransaction.booking_code}</strong>? 
                This action cannot be undone.
              </p>
              
              <div className="flex justify-end space-x-3">
                <button
                  onClick={cancelDelete}
                  className="px-4 py-2 text-gray-700 border border-gray-300 rounded-md hover:bg-gray-50"
                  disabled={deleteMutation.isLoading}
                >
                  Cancel
                </button>
                <button
                  onClick={confirmDelete}
                  className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50"
                  disabled={deleteMutation.isLoading}
                >
                  {deleteMutation.isLoading ? 'Deleting...' : 'Delete'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminTransactions
