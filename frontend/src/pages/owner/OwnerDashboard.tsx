import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ownerService } from '../../services/ownerService';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import StatusBadge from '../../components/StatusBadge';
import type { OwnerDashboardData, OrderStatus } from '../../types';

const OwnerDashboard = () => {
  const [data, setData] = useState<OwnerDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    ownerService.getDashboardData()
      .then(setData)
      .catch(err => setError(err.response?.data?.message || 'Failed to load dashboard.'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner message="Loading dashboard..." />;

  // Render nothing if no data and no error yet (though LoadingSpinner covers this)
  if (!data && !error) return null;

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Owner Dashboard</h1>
      <p className="text-gray-500 mb-8">Manage your restaurants, menu, and incoming orders</p>

      {error && <ErrorMessage message={error} />}

      {data && (
        <>
          {/* Quick stats */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
            <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
              <p className="text-sm text-gray-500 mb-1">Active Orders</p>
              <p className="text-3xl font-bold text-orange-500">{data.activeOrderCount}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
              <p className="text-sm text-gray-500 mb-1">Completed Orders</p>
              <p className="text-3xl font-bold text-green-600">{data.completedOrderCount}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
              <p className="text-sm text-gray-500 mb-1">Total Revenue</p>
              <p className="text-3xl font-bold text-blue-600">₹{data.totalRevenue.toFixed(2)}</p>
            </div>
            <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
              <p className="text-sm text-gray-500 mb-1">My Restaurants</p>
              <p className="text-3xl font-bold text-gray-700">{data.restaurants.length}</p>
            </div>
          </div>

          {/* My Restaurants */}
          <section className="mb-8">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-900">My Restaurants</h2>
              <Link to="/owner/menu" className="text-blue-600 hover:underline text-sm font-medium">Manage Menu →</Link>
            </div>
            {data.restaurants.length === 0 ? (
              <div className="bg-white rounded-xl border border-gray-100 p-6 text-center text-gray-500">
                You don't have any restaurants assigned yet. Contact your admin.
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {data.restaurants.map(r => (
                  <div key={r.id} className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
                    <div className="flex justify-between items-start">
                      <div>
                        <h3 className="font-bold text-gray-900">{r.name}</h3>
                        <p className="text-sm text-gray-500">{r.cuisineType} · {r.city}</p>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${r.open ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
                          {r.open ? 'Open' : 'Closed'}
                        </span>
                        <Link to={`/owner/restaurant/${r.id}`} className="text-xs text-blue-600 hover:underline">Edit Details →</Link>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>

          {/* Recent Orders preview */}
          <section>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-900">Recent Orders</h2>
              <Link to="/owner/orders" className="text-blue-600 hover:underline text-sm font-medium">View All Orders →</Link>
            </div>
            {data.recentOrders.length === 0 ? (
              <div className="bg-white rounded-xl border border-gray-100 p-6 text-center text-gray-500">
                No orders yet.
              </div>
            ) : (
              <div className="space-y-3">
                {data.recentOrders.map(o => (
                  <div key={o.orderId} className="bg-white rounded-xl border border-gray-100 shadow-sm p-4 flex justify-between items-center">
                    <div>
                      <span className="font-semibold text-gray-900">Order #{o.orderId}</span>
                      <p className="text-xs text-gray-400 mt-0.5">{new Date(o.createdAt).toLocaleString('en-IN')}</p>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="font-bold text-gray-900">₹{o.totalAmount.toFixed(2)}</span>
                      <StatusBadge status={o.status as OrderStatus} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
};

export default OwnerDashboard;
