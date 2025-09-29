import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AuthProvider } from './context/AuthContext'

// Import pages
import HomePage from './pages/HomePage'
import OfficesPage from './pages/OfficesPage'
import OfficeDetailPage from './pages/OfficeDetailPage'
import BookingPage from './pages/BookingPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import AdminLayout from './components/AdminLayout'
import AdminDashboard from './pages/admin/AdminDashboard'
import AdminOffices from './pages/admin/AdminOffices'
import AdminTransactions from './pages/admin/AdminTransactions'

// Import components
import Navbar from './components/Navbar'
import Footer from './components/Footer'

// Create a client for React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <div className="min-h-screen bg-gray-50">
            <Routes>
              {/* Admin Routes */}
              <Route path="/admin" element={<AdminLayout />}>
                <Route index element={<AdminDashboard />} />
                <Route path="offices" element={<AdminOffices />} />
                <Route path="transactions" element={<AdminTransactions />} />
              </Route>
              
              {/* Public Routes */}
              <Route path="/*" element={
                <>
                  <Navbar />
                  <main className="flex-1">
                    <Routes>
                      <Route path="/" element={<HomePage />} />
                      <Route path="/offices" element={<OfficesPage />} />
                      <Route path="/offices/:id" element={<OfficeDetailPage />} />
                      <Route path="/booking/:officeId" element={<BookingPage />} />
                      <Route path="/login" element={<LoginPage />} />
                      <Route path="/register" element={<RegisterPage />} />
                      <Route path="/dashboard" element={<DashboardPage />} />
                    </Routes>
                  </main>
                  <Footer />
                </>
              } />
            </Routes>
          </div>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
    
  )
}

export default App
