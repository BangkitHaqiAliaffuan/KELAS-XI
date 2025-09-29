import React, { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Search, Filter, MapPin, Users, Star, ArrowRight } from 'lucide-react'
import { officesApi, citiesApi, facilitiesApi } from '../services/apiService'
import { formatCurrency, debounce } from '../utils/helpers'

const OfficesPage = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const [filters, setFilters] = useState({
    search: searchParams.get('search') || '',
    city_id: searchParams.get('city_id') || '',
    min_capacity: searchParams.get('min_capacity') || '',
    max_capacity: searchParams.get('max_capacity') || '',
    min_price: searchParams.get('min_price') || '',
    max_price: searchParams.get('max_price') || '',
    facilities: searchParams.get('facilities') || '',
    sort_by: searchParams.get('sort_by') || 'created_at',
    sort_order: searchParams.get('sort_order') || 'desc',
  })
  const [currentPage, setCurrentPage] = useState(parseInt(searchParams.get('page')) || 1)
  const [showFilters, setShowFilters] = useState(false)

  // Fetch offices with filters
  const { data: officesData, isLoading } = useQuery({
    queryKey: ['offices', { ...filters, page: currentPage }],
    queryFn: () => officesApi.getAll({ ...filters, page: currentPage, per_page: 12 }),
  })

  // Fetch cities for filter
  const { data: citiesData } = useQuery({
    queryKey: ['cities'],
    queryFn: () => citiesApi.getAll(),
  })

  // Fetch facilities for filter
  const { data: facilitiesData } = useQuery({
    queryKey: ['facilities'],
    queryFn: () => facilitiesApi.getAll(),
  })

  const offices = officesData?.data?.data || []
  const pagination = officesData?.data?.pagination || {}
  const cities = citiesData?.data?.data || []
  const facilities = facilitiesData?.data?.data || []

  // Update URL when filters change
  useEffect(() => {
    const params = new URLSearchParams()
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.set(key, value)
    })
    if (currentPage > 1) params.set('page', currentPage.toString())
    setSearchParams(params)
  }, [filters, currentPage, setSearchParams])

  // Debounced search
  const debouncedSearch = debounce((value) => {
    setFilters(prev => ({ ...prev, search: value }))
    setCurrentPage(1)
  }, 500)

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }))
    setCurrentPage(1)
  }

  const handleSearchChange = (e) => {
    const value = e.target.value
    setFilters(prev => ({ ...prev, search: value }))
    debouncedSearch(value)
  }

  const clearFilters = () => {
    setFilters({
      search: '',
      city_id: '',
      min_capacity: '',
      max_capacity: '',
      min_price: '',
      max_price: '',
      facilities: '',
      sort_by: 'created_at',
      sort_order: 'desc',
    })
    setCurrentPage(1)
  }

  const sortOptions = [
    { value: 'created_at:desc', label: 'Terbaru' },
    { value: 'created_at:asc', label: 'Terlama' },
    { value: 'price_per_day:asc', label: 'Harga Terendah' },
    { value: 'price_per_day:desc', label: 'Harga Tertinggi' },
    { value: 'rating:desc', label: 'Rating Tertinggi' },
    { value: 'name:asc', label: 'Nama A-Z' },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">
            Daftar Kantor
          </h1>
          <p className="text-gray-600">
            Temukan ruang kantor yang sesuai dengan kebutuhan Anda
          </p>
        </div>

        {/* Search and Filter Bar */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex flex-col lg:flex-row gap-4">
            {/* Search Input */}
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="Cari kantor berdasarkan nama atau lokasi..."
                  value={filters.search}
                  onChange={handleSearchChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            {/* Sort Dropdown */}
            <div className="w-full lg:w-64">
              <select
                value={`${filters.sort_by}:${filters.sort_order}`}
                onChange={(e) => {
                  const [sort_by, sort_order] = e.target.value.split(':')
                  handleFilterChange('sort_by', sort_by)
                  handleFilterChange('sort_order', sort_order)
                }}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {sortOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Filter Toggle */}
            <button
              onClick={() => setShowFilters(!showFilters)}
              className="flex items-center justify-center px-6 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <Filter className="h-5 w-5 mr-2" />
              Filter
            </button>
          </div>

          {/* Advanced Filters */}
          {showFilters && (
            <div className="mt-6 pt-6 border-t border-gray-200">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {/* City Filter */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Kota
                  </label>
                  <select
                    value={filters.city_id}
                    onChange={(e) => handleFilterChange('city_id', e.target.value)}
                    className="input-field"
                  >
                    <option value="">Semua Kota</option>
                    {cities.map(city => (
                      <option key={city.id} value={city.id}>
                        {city.name}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Capacity Filter */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Kapasitas
                  </label>
                  <div className="flex space-x-2">
                    <input
                      type="number"
                      placeholder="Min"
                      value={filters.min_capacity}
                      onChange={(e) => handleFilterChange('min_capacity', e.target.value)}
                      className="input-field"
                    />
                    <input
                      type="number"
                      placeholder="Max"
                      value={filters.max_capacity}
                      onChange={(e) => handleFilterChange('max_capacity', e.target.value)}
                      className="input-field"
                    />
                  </div>
                </div>

                {/* Price Filter */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Harga per Hari (Rp)
                  </label>
                  <div className="flex space-x-2">
                    <input
                      type="number"
                      placeholder="Min"
                      value={filters.min_price}
                      onChange={(e) => handleFilterChange('min_price', e.target.value)}
                      className="input-field"
                    />
                    <input
                      type="number"
                      placeholder="Max"
                      value={filters.max_price}
                      onChange={(e) => handleFilterChange('max_price', e.target.value)}
                      className="input-field"
                    />
                  </div>
                </div>

                {/* Facilities Filter */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Fasilitas
                  </label>
                  <select
                    value={filters.facilities}
                    onChange={(e) => handleFilterChange('facilities', e.target.value)}
                    className="input-field"
                  >
                    <option value="">Semua Fasilitas</option>
                    {facilities.map(facility => (
                      <option key={facility.id} value={facility.id}>
                        {facility.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="mt-4 flex justify-end">
                <button
                  onClick={clearFilters}
                  className="btn-secondary mr-4"
                >
                  Reset Filter
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Results */}
        <div className="mb-6">
          <p className="text-gray-600">
            Menampilkan {pagination.total || 0} kantor
            {filters.search && ` untuk "${filters.search}"`}
          </p>
        </div>

        {/* Office Grid */}
        {isLoading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[...Array(6)].map((_, index) => (
              <div key={index} className="card animate-pulse">
                <div className="w-full h-48 bg-gray-300"></div>
                <div className="p-6">
                  <div className="h-4 bg-gray-300 rounded mb-2"></div>
                  <div className="h-3 bg-gray-300 rounded mb-4"></div>
                  <div className="h-3 bg-gray-300 rounded"></div>
                </div>
              </div>
            ))}
          </div>
        ) : offices.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">
              Tidak ada kantor yang ditemukan dengan kriteria pencarian Anda.
            </p>
            <button
              onClick={clearFilters}
              className="btn-primary mt-4"
            >
              Reset Pencarian
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
            {offices.map((office) => (
              <Link
                key={office.id}
                to={`/offices/${office.id}`}
                className="card hover:shadow-lg transition-shadow duration-300"
              >
                <div className="aspect-w-16 aspect-h-9">
                  <img
                    src={office.main_photo || '/placeholder-office.jpg'}
                    alt={office.name}
                    className="w-full h-48 object-cover"
                  />
                </div>
                <div className="p-6">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="text-lg font-semibold text-gray-900">
                      {office.name}
                    </h3>
                    <div className="flex items-center">
                      <Star className="h-4 w-4 text-yellow-400 fill-current" />
                      <span className="text-sm text-gray-600 ml-1">
                        {office.rating || 0}
                      </span>
                    </div>
                  </div>
                  
                  <div className="flex items-center text-gray-600 mb-2">
                    <MapPin className="h-4 w-4 mr-1" />
                    <span className="text-sm">{office.city?.name}</span>
                  </div>
                  
                  <div className="flex items-center text-gray-600 mb-4">
                    <Users className="h-4 w-4 mr-1" />
                    <span className="text-sm">Kapasitas {office.capacity} orang</span>
                  </div>

                  <div className="border-t pt-4">
                    <div className="flex items-center justify-between">
                      <div>
                        <span className="text-2xl font-bold text-blue-600">
                          {formatCurrency(office.price_per_day)}
                        </span>
                        <span className="text-gray-600 text-sm">/hari</span>
                      </div>
                      <ArrowRight className="h-5 w-5 text-blue-600" />
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}

        {/* Pagination */}
        {pagination.last_page > 1 && (
          <div className="flex justify-center">
            <div className="flex space-x-2">
              <button
                onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                disabled={currentPage === 1}
                className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Previous
              </button>
              
              {[...Array(Math.min(pagination.last_page, 5))].map((_, index) => {
                const page = index + 1
                return (
                  <button
                    key={page}
                    onClick={() => setCurrentPage(page)}
                    className={`px-4 py-2 border rounded-lg ${
                      currentPage === page
                        ? 'bg-blue-600 text-white border-blue-600'
                        : 'border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    {page}
                  </button>
                )
              })}
              
              <button
                onClick={() => setCurrentPage(prev => Math.min(prev + 1, pagination.last_page))}
                disabled={currentPage === pagination.last_page}
                className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default OfficesPage
