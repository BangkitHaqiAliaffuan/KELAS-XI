import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { 
  Building2, 
  Plus, 
  Edit, 
  Trash2, 
  Search,
  Filter,
  MapPin,
  Eye
} from 'lucide-react'
import { officeService } from '../../services/api'

const AdminOffices = () => {
  const queryClient = useQueryClient()
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCity, setSelectedCity] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editingOffice, setEditingOffice] = useState(null)

  const { data: offices, isLoading } = useQuery({
    queryKey: ['offices', searchTerm, selectedCity],
    queryFn: () => officeService.getAll({ 
      search: searchTerm,
      city_id: selectedCity 
    })
  })

  const deleteMutation = useMutation({
    mutationFn: officeService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries(['offices'])
    }
  })

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this office?')) {
      deleteMutation.mutate(id)
    }
  }

  const handleEdit = (office) => {
    setEditingOffice(office)
    setShowForm(true)
  }

  const handleAdd = () => {
    setEditingOffice(null)
    setShowForm(true)
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

  return (
    <div className="p-6">
      <div className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Offices Management</h1>
            <p className="text-gray-600">Manage all office spaces and their details</p>
          </div>
          <button
            onClick={handleAdd}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 flex items-center"
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
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
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
                src="https://images.unsplash.com/photo-1497366216548-37526070297c?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80"
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
                    onClick={() => handleDelete(office.id)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded-md transition-colors"
                    title="Delete"
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

      {/* Office Form Modal - Placeholder */}
      {showForm && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-lg max-w-md w-full p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              {editingOffice ? 'Edit Office' : 'Add New Office'}
            </h3>
            <p className="text-gray-600 mb-4">
              Office form will be implemented here with all necessary fields.
            </p>
            <div className="flex justify-end space-x-2">
              <button
                onClick={() => setShowForm(false)}
                className="px-4 py-2 text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300"
              >
                Cancel
              </button>
              <button className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
                {editingOffice ? 'Update' : 'Create'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminOffices
