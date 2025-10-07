import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import AdminRoutes from './routes/AdminRoutes'
import PublicRoutes from './routes/PublicRoutes'

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
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Routes>
            {/* Admin Routes - Must be FIRST to prevent Public routes from catching /admin */}
            <Route path="/admin/*" element={<AdminRoutes />} />

            {/* Public Routes - Will NOT match /admin/* because it's already matched above */}
            <Route path="*" element={<PublicRoutes />} />
          </Routes>
        </div>
      </Router>
    </QueryClientProvider>
  )
}

export default App
