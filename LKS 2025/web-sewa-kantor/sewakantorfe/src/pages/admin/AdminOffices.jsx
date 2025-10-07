import React, { useState, useEffect, useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { 
  Building2, 
  Plus, 
  Edit, 
  Trash2, 
  Search,
  Filter,
  MapPin,
  Eye,
  AlertCircle,
  CheckCircle
} from 'lucide-react'
import { officeService } from '../../services/api'
import OfficeFormModal from '../../components/OfficeFormModal'
import DeleteConfirmModal from '../../components/DeleteConfirmModal'

const AdminOffices = () => {
  const queryClient = useQueryClient()
  const [searchTerm, setSearchTerm] = useState('')
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('')
  const [selectedCity, setSelectedCity] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editingOffice, setEditingOffice] = useState(null)
  const [notification, setNotification] = useState({ show: false, message: '', type: '' })
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [deletingOffice, setDeletingOffice] = useState(null)

  // Debounce search term to prevent excessive API calls
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm)
    }, 500) // 500ms delay

    return () => clearTimeout(timer)
  }, [searchTerm])

  const { data: offices, isLoading, error } = useQuery({
    queryKey: ['offices', debouncedSearchTerm, selectedCity],
    queryFn: () => officeService.getAll({ 
      search: debouncedSearchTerm,
      city_id: selectedCity 
    }),
    retry: 2,
    refetchOnWindowFocus: false
  })

  // Create Office Mutation
  const createMutation = useMutation({
    mutationFn: officeService.create,
    onSuccess: (response) => {
      queryClient.invalidateQueries(['offices'])
      setShowForm(false)
      setEditingOffice(null)
      showNotification('Office created successfully!', 'success')
    },
    onError: (error) => {
      console.error('Create office error:', error)
      showNotification(
        error.response?.data?.message || 'Failed to create office', 
        'error'
      )
    }
  })

  // Update Office Mutation
  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => officeService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['offices'])
      setShowForm(false)
      setEditingOffice(null)
      showNotification('Office updated successfully!', 'success')
    },
    onError: (error) => {
      console.error('Update office error:', error)
      showNotification(
        error.response?.data?.message || 'Failed to update office', 
        'error'
      )
    }
  })

  // Delete Office Mutation
  const deleteMutation = useMutation({
    mutationFn: officeService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['offices'])
      showNotification('Office deleted successfully!', 'success')
    },
    onError: (error) => {
      console.error('Delete office error:', error)
      showNotification(
        error.response?.data?.message || 'Failed to delete office', 
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

  const handleDelete = (office) => {
    setDeletingOffice(office)
    setShowDeleteModal(true)
  }

  const confirmDelete = () => {
    if (deletingOffice) {
      deleteMutation.mutate(deletingOffice.id)
      setShowDeleteModal(false)
      setDeletingOffice(null)
    }
  }

  const cancelDelete = () => {
    setShowDeleteModal(false)
    setDeletingOffice(null)
  }

  const handleEdit = (office) => {
    setEditingOffice(office)
    setShowForm(true)
  }

  const handleAdd = () => {
    setEditingOffice(null)
    setShowForm(true)
  }

  const handleFormSubmit = async (formData) => {
    try {
      if (editingOffice) {
        // Update existing office
        await updateMutation.mutateAsync({ 
          id: editingOffice.id, 
          data: formData 
        })
      } else {
        // Create new office
        await createMutation.mutateAsync(formData)
      }
    } catch (error) {
      // Error handling is done in mutation onError callbacks
      console.error('Form submit error:', error)
    }
  }

  const [isInitialLoadState, setIsInitialLoadState] = useState(true)
  const isInitialLoad = isInitialLoadState && !offices

  useEffect(() => {
    if (offices) {
      setIsInitialLoadState(false)
    }
  }, [offices])

  const handleCloseForm = () => {
    setShowForm(false)
    setEditingOffice(null)
  }

  if (isInitialLoad && isLoading) {
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

  if (error && isInitialLoad) {
    return (
      <div className="p-6">
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <div className="flex items-center">
            <AlertCircle className="h-5 w-5 text-red-400 mr-2" />
            <h3 className="text-sm font-medium text-red-800">Error loading offices</h3>
          </div>
          <p className="text-sm text-red-700 mt-2">
            {error.response?.data?.message || 'Failed to load offices. Please try again.'}
          </p>
          <button
            onClick={() => {
              queryClient.invalidateQueries(['offices'])
              setIsInitialLoadState(true) // Reset initial load state
            }}
            className="mt-3 text-sm bg-red-100 text-red-800 px-3 py-1 rounded hover:bg-red-200"
          >
            Retry
          </button>
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
          <p className={`text-sm font-medium ${
            notification.type === 'success' ? 'text-green-800' : 'text-red-800'
          }`}>
            {notification.message}
          </p>
        </div>
      )}

      <div className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Offices Management</h1>
            <p className="text-gray-600">Manage all office spaces and their details</p>
          </div>
          <button
            onClick={handleAdd}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 flex items-center transition-colors"
            disabled={createMutation.isLoading}
          >
            <Plus className="h-5 w-5 mr-2" />
            Add Office
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow mb-6 p-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              type="text"
              placeholder="Search offices..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {isLoading && debouncedSearchTerm !== '' && (
              <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-500"></div>
              </div>
            )}
          </div>
          
          <select
            value={selectedCity}
            onChange={(e) => setSelectedCity(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Cities</option>
            <option value="1">Jakarta</option>
            <option value="2">Surabaya</option>
            <option value="3">Bandung</option>
          </select>

          <div className="flex items-center space-x-2">
            <Filter className="h-5 w-5 text-gray-400" />
            <span className="text-sm text-gray-600">
              {offices?.data?.length || 0} offices found
            </span>
          </div>
        </div>
      </div>

      {/* Offices Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {offices?.data?.map((office) => (
          <div key={office.id} className="bg-white rounded-lg shadow hover:shadow-md transition-shadow">
            <div className="h-48 bg-gray-200 rounded-t-lg relative">
              <img 
              
                src={office.photos[0]? office.photos[0] : 'https://images.unsplash.com/photo-1497366216548-37526070297c?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80'}
                alt={office.name}
                className="w-full h-full object-cover rounded-t-lg"
              />
              <div className="absolute top-4 right-4">
                <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                  office.is_available 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {office.is_available ? 'Available' : 'Occupied'}
                </span>
              </div>
            </div>
            
            <div className="p-4">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">{office.name}</h3>
              <div className="flex items-center text-gray-600 mb-2">
                <MapPin className="h-4 w-4 mr-1" />
                <span className="text-sm">{office.address}</span>
              </div>
              
              <div className="mb-4">
                <span className="text-2xl font-bold text-blue-600">
                  Rp {office.price_per_day?.toLocaleString('id-ID') || office.price?.toLocaleString('id-ID')}
                </span>
                <span className="text-gray-600"> / day</span>
              </div>

              <div className="flex justify-between items-center">
                <div className="flex space-x-2">
                  <button
                    onClick={() => handleEdit(office)}
                    className="p-2 text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
                    title="Edit"
                  >
                    <Edit className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(office)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded-md transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    title="Delete"
                    disabled={deleteMutation.isLoading}
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                  <button className="p-2 text-gray-600 hover:bg-gray-50 rounded-md transition-colors" title="View">
                    <Eye className="h-4 w-4" />
                  </button>
                </div>
                <div className="text-sm text-gray-500">
                  ID: {office.id}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {offices?.data?.length === 0 && (
        <div className="text-center py-12">
          <Building2 className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No offices found</h3>
          <p className="text-gray-600 mb-4">Get started by adding your first office space.</p>
          <button
            onClick={handleAdd}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            Add Office
          </button>
        </div>
      )}

      {/* Office Form Modal */}
      <OfficeFormModal
        isOpen={showForm}
        onClose={handleCloseForm}
        office={editingOffice}
        onSubmit={handleFormSubmit}
        isLoading={createMutation.isLoading || updateMutation.isLoading}
      />

      {/* Delete Confirmation Modal */}
      <DeleteConfirmModal
        isOpen={showDeleteModal}
        onClose={cancelDelete}
        onConfirm={confirmDelete}
        title="Delete Office"
        message="Are you sure you want to delete this office?"
        itemName={deletingOffice?.name}
        isLoading={deleteMutation.isLoading}
      />
    </div>
  )
}

export default AdminOffices
