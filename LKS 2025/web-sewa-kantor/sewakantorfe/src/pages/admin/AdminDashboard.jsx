import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { 
  Building2, 
  Receipt, 
  Users, 
  TrendingUp,
  Calendar,
  DollarSign
} from 'lucide-react'
import { transactionService, officeService } from '../../services/api'

const AdminDashboard = () => {
  const { data: transactions } = useQuery({
    queryKey: ['transactions'],
    queryFn: transactionService.getAll
  })

  const { data: offices } = useQuery({
    queryKey: ['offices'],
    queryFn: officeService.getAll
  })

  // Mock data for demonstration
  const stats = [
    {
      name: 'Total Offices',
      value: offices?.data?.length || 0,
      icon: Building2,
      color: 'bg-blue-500',
      change: '+12%',
      changeType: 'increase'
    },
    {
      name: 'Total Bookings',
      value: transactions?.data?.length || 0,
      icon: Receipt,
      color: 'bg-green-500',
      change: '+18%',
      changeType: 'increase'
    },
    {
      name: 'Active Users',
      value: 89,
      icon: Users,
      color: 'bg-purple-500',
      change: '+23%',
      changeType: 'increase'
    },
    {
      name: 'Monthly Revenue',
      value: 'Rp 45.2M',
      icon: DollarSign,
      color: 'bg-yellow-500',
      change: '+8%',
      changeType: 'increase'
    }
  ]

  const recentTransactions = transactions?.data?.slice(0, 5) || []

  return (
    <div className="p-6">
      
      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat) => {
          const Icon = stat.icon
          return (
            <div key={stat.name} className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className={`${stat.color} rounded-md p-3 mr-4`}>
                  <Icon className="h-6 w-6 text-white" />
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                  <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                </div>
              </div>
              <div className="mt-4 flex items-center">
                <span className={`text-sm font-medium ${
                  stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {stat.change}
                </span>
                <span className="text-sm text-gray-500 ml-2">from last month</span>
              </div>
            </div>
          )
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Transactions */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b border-gray-200">
            <h3 className="text-lg font-medium text-gray-900">Recent Transactions</h3>
          </div>
          <div className="divide-y divide-gray-200">
            {recentTransactions.length > 0 ? (
              recentTransactions.map((transaction, index) => (
                <div key={index} className="p-6 flex items-center justify-between">
                  <div className="flex items-center">
                    <div className="bg-blue-100 rounded-full p-2 mr-4">
                      <Receipt className="h-4 w-4 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-900">
                        Transaction #{transaction.id}
                      </p>
                      <p className="text-sm text-gray-500">
                        {new Date(transaction.created_at).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium text-gray-900">
                      Rp {transaction.total_amount?.toLocaleString('id-ID')}
                    </p>
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      transaction.status === 'completed' 
                        ? 'bg-green-100 text-green-800' 
                        : transaction.status === 'pending'
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {transaction.status}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <div className="p-6 text-center text-gray-500">
                No recent transactions
              </div>
            )}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b border-gray-200">
            <h3 className="text-lg font-medium text-gray-900">Quick Actions</h3>
          </div>
          <div className="p-6 space-y-4">
            <button className="w-full flex items-center justify-between p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors">
              <div className="flex items-center">
                <Building2 className="h-5 w-5 text-blue-600 mr-3" />
                <span className="font-medium text-blue-900">Add New Office</span>
              </div>
              <span className="text-blue-600">→</span>
            </button>
            
            <button className="w-full flex items-center justify-between p-4 bg-green-50 rounded-lg hover:bg-green-100 transition-colors">
              <div className="flex items-center">
                <Receipt className="h-5 w-5 text-green-600 mr-3" />
                <span className="font-medium text-green-900">View All Transactions</span>
              </div>
              <span className="text-green-600">→</span>
            </button>
            
            <button className="w-full flex items-center justify-between p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors">
              <div className="flex items-center">
                <Users className="h-5 w-5 text-purple-600 mr-3" />
                <span className="font-medium text-purple-900">Manage Users</span>
              </div>
              <span className="text-purple-600">→</span>
            </button>
            
            <button className="w-full flex items-center justify-between p-4 bg-yellow-50 rounded-lg hover:bg-yellow-100 transition-colors">
              <div className="flex items-center">
                <TrendingUp className="h-5 w-5 text-yellow-600 mr-3" />
                <span className="font-medium text-yellow-900">View Reports</span>
              </div>
              <span className="text-yellow-600">→</span>
            </button>
          </div>
        </div>
      </div>

      {/* Chart placeholder */}
      <div className="mt-8 bg-white rounded-lg shadow">
        <div className="p-6 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">Revenue Chart</h3>
        </div>
        <div className="p-6">
          <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center">
            <div className="text-center">
              <TrendingUp className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">Revenue chart will be displayed here</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard
