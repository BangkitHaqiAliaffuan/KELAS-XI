import React from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { 
  LayoutDashboard, 
  Building2, 
  Receipt, 
  Users, 
  Settings,
  LogOut,
  Menu,
  X
} from 'lucide-react'
import { useAdminAuth } from '../context/AdminAuthContext'

const AdminLayout = () => {
  const navigate = useNavigate()
  const { logout, admin, loading } = useAdminAuth()
  const [sidebarOpen, setSidebarOpen] = React.useState(false)

  // Redirect to admin login if not authenticated
  React.useEffect(() => {
    if (!loading && !admin) {
      navigate('/admin/login', { replace: true });
    }
  }, [loading, admin, navigate]);

  const handleLogout = async () => {
    await logout()
    navigate('/admin/login', { replace: true })
  }

  // Show loading state while checking authentication
  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    )
  }

  // Don't render layout if not authenticated
  if (!admin) {
    return null
  }

  const navigation = [
    { name: 'Dashboard', href: '/admin', icon: LayoutDashboard },
    { name: 'Offices', href: '/admin/offices', icon: Building2 },
    { name: 'Transactions', href: '/admin/transactions', icon: Receipt },
    { name: 'Users', href: '/admin/users', icon: Users },
    { name: 'Settings', href: '/admin/settings', icon: Settings },
  ]

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Mobile sidebar overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        >
          <div className="fixed inset-0 bg-gray-600 bg-opacity-75" />
        </div>
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      }`}>
        <div className="flex flex-col h-full">
          {/* Logo/Brand */}
          <div className="flex items-center justify-between h-16 px-6 bg-blue-600 flex-shrink-0">
            <h1 className="text-xl font-bold text-white">Office Admin</h1>
            <button
              onClick={() => setSidebarOpen(false)}
              className="lg:hidden text-white hover:text-gray-200"
            >
              <X className="h-6 w-6" />
            </button>
          </div>
          
          {/* Navigation */}
          <nav className="flex-1 overflow-y-auto py-4">
            <div className="px-4 space-y-1">
              {navigation.map((item) => {
                const Icon = item.icon
                return (
                  <NavLink
                    key={item.name}
                    to={item.href}
                    end={item.href === '/admin'}
                    onClick={() => setSidebarOpen(false)}
                    className={({ isActive }) =>
                      `flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${
                        isActive
                          ? 'bg-blue-50 text-blue-700'
                          : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
                      }`
                    }
                  >
                    <Icon className="mr-3 h-5 w-5" />
                    {item.name}
                  </NavLink>
                )
              })}
            </div>
          </nav>

          {/* User info and logout */}
          <div className="flex-shrink-0 p-4 border-t border-gray-200 bg-white">
            <div className="mb-3 px-3 py-2 bg-gray-50 rounded-lg">
              <p className="text-sm font-medium text-gray-900 truncate">{admin?.name || 'Admin'}</p>
              <p className="text-xs text-gray-600 truncate">{admin?.email || 'admin@sewakantor.com'}</p>
              {admin?.role && (
                <p className="text-xs text-blue-600 font-medium mt-1 capitalize">{admin.role.replace('_', ' ')}</p>
              )}
            </div>
            <button
              onClick={handleLogout}
              className="flex items-center w-full px-4 py-2 text-sm font-medium text-red-700 rounded-lg hover:bg-red-50 transition-colors"
            >
              <LogOut className="mr-3 h-5 w-5" />
              Logout
            </button>
          </div>
        </div>
      </div>

      {/* Main content area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top header */}
        <header className="bg-white shadow-sm border-b border-gray-200 flex-shrink-0">
          <div className="flex items-center justify-between h-16 px-6">
            <button
              onClick={() => setSidebarOpen(true)}
              className="lg:hidden text-gray-600 hover:text-gray-900"
            >
              <Menu className="h-6 w-6" />
            </button>
            
            <div className="hidden lg:block">
              {/* Breadcrumb or page title can go here */}
            </div>
          </div>
        </header>

        {/* Page content - scrollable */}
        <main className="flex-1 overflow-y-auto bg-gray-50">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

export default AdminLayout
