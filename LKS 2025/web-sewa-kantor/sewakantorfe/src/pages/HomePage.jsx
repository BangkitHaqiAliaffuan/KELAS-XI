import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Search, MapPin, Users, Clock, Star, ArrowRight } from 'lucide-react'
import { citiesApi, officesApi } from '../services/apiService'
import { formatCurrency } from '../utils/helpers'

const HomePage = () => {
  const [searchQuery, setSearchQuery] = useState('')
  const navigate = useNavigate()

  // Fetch cities for homepage
  const { data: citiesData } = useQuery({
    queryKey: ['cities'],
    queryFn: () => citiesApi.getAll(),
  })

  // Fetch featured offices
  const { data: featuredOfficesData } = useQuery({
    queryKey: ['featured-offices'],
    queryFn: () => officesApi.getAll({ per_page: 6, sort_by: 'rating', sort_order: 'desc' }),
  })

  const cities = citiesData?.data?.data || []
  const featuredOffices = featuredOfficesData?.data?.data || []

  const handleSearch = (e) => {
    e.preventDefault()
    if (searchQuery.trim()) {
      navigate(`/offices?search=${encodeURIComponent(searchQuery.trim())}`)
    }
  }

  const features = [
    {
      icon: <Search className="h-8 w-8 text-blue-600" />,
      title: 'Pencarian Mudah',
      description: 'Temukan kantor sesuai kebutuhan dengan filter lokasi, harga, dan fasilitas'
    },
    {
      icon: <Clock className="h-8 w-8 text-blue-600" />,
      title: 'Booking Instant',
      description: 'Proses booking yang cepat dan mudah, konfirmasi dalam hitungan menit'
    },
    {
      icon: <Users className="h-8 w-8 text-blue-600" />,
      title: 'Fleksible',
      description: 'Sewa harian, mingguan, atau bulanan sesuai kebutuhan bisnis Anda'
    },
  ]

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-blue-600 to-blue-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6">
              Temukan Ruang Kantor
              <br />
              <span className="text-primary-200">Impian Anda</span>
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-primary-100">
              Platform terpercaya untuk menyewa ruang kantor berkualitas dengan harga terjangkau
            </p>

            {/* Search Form */}
            <form onSubmit={handleSearch} className="max-w-2xl mx-auto">
              <div className="flex flex-col md:flex-row gap-4">
                <div className="flex-1">
                  <input
                    type="text"
                    placeholder="Cari kantor berdasarkan lokasi atau nama..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="w-full px-6 py-4 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-400"
                  />
                </div>
                <button
                  type="submit"
                  className="bg-secondary-600 hover:bg-secondary-700 text-white px-8 py-4 rounded-lg font-semibold flex items-center justify-center transition-colors"
                >
                  <Search className="h-5 w-5 mr-2" />
                  Cari Kantor
                </button>
              </div>
            </form>
          </div>
        </div>
      </section>

      {/* Cities Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Pilih Kota Tujuan
            </h2>
            <p className="text-xl text-gray-600">
              Tersedia di berbagai kota besar di Indonesia
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {cities.map((city) => (
              <Link
                key={city.id}
                to={`/offices?city_id=${city.id}`}
                className="card hover:shadow-lg transition-shadow duration-300"
              >
                <div className="aspect-w-16 aspect-h-9">
                  <img
                    src={city.photo || '/placeholder-city.jpg'}
                    alt={city.name}
                    className="w-full h-48 object-cover"
                  />
                </div>
                <div className="p-6">
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">
                    {city.name}
                  </h3>
                  <p className="text-gray-600 mb-4">
                    {city.description}
                  </p>
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">
                      {city.offices_count || 0} kantor tersedia
                    </span>
                    <ArrowRight className="h-5 w-5 text-blue-600" />
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* Featured Offices Section */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Kantor Pilihan
            </h2>
            <p className="text-xl text-gray-600">
              Kantor-kantor terbaik dengan rating tertinggi
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {featuredOffices.map((office) => (
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

          <div className="text-center mt-12">
            <Link
              to="/offices"
              className="btn-primary inline-flex items-center"
            >
              Lihat Semua Kantor
              <ArrowRight className="h-5 w-5 ml-2" />
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Mengapa Pilih Kami?
            </h2>
            <p className="text-xl text-gray-600">
              Kemudahan dan keamanan dalam setiap transaksi
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {features.map((feature, index) => (
              <div key={index} className="text-center">
                <div className="flex justify-center mb-4">
                  {feature.icon}
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-4">
                  {feature.title}
                </h3>
                <p className="text-gray-600">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-blue-600 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-4">
            Siap Memulai Bisnis Anda?
          </h2>
          <p className="text-xl mb-8 text-primary-100">
            Temukan ruang kantor yang sempurna untuk mengembangkan bisnis Anda
          </p>
          <Link
            to="/offices"
            className="bg-white text-blue-600 hover:bg-gray-100 px-8 py-4 rounded-lg font-semibold inline-flex items-center transition-colors"
          >
            Mulai Pencarian
            <ArrowRight className="h-5 w-5 ml-2" />
          </Link>
        </div>
      </section>
    </div>
  )
}

export default HomePage
