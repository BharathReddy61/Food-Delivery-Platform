import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import StatusBadge from '../../components/StatusBadge';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Order } from '../../types';

const Orders = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    orderService.getUserOrders()
      .then(setOrders)
      .catch(err => setError(err.response?.data?.message || 'Failed to load orders.'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner message="Loading your orders..." />;

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Order History</h1>

      {error && <ErrorMessage message={error} />}

      {!error && orders.length === 0 && (
        <div className="text-center py-16 bg-white rounded-xl border border-gray-100 shadow-sm">
          <p className="text-5xl mb-4">📋</p>
          <p className="text-gray-600 text-lg font-medium mb-2">No orders yet</p>
          <Link to="/restaurants" className="text-blue-600 hover:underline text-sm">
            Browse restaurants to place your first order
          </Link>
        </div>
      )}

      <div className="space-y-4">
        {orders.map(order => (
          <Link
            key={order.orderId}
            to={`/orders/${order.orderId}`}
            className="block bg-white rounded-xl border border-gray-100 shadow-sm p-5 hover:shadow-md transition-shadow"
          >
            <div className="flex justify-between items-center mb-3">
              <span className="font-bold text-gray-900">Order #{order.orderId}</span>
              <StatusBadge status={order.status} />
            </div>
            <p className="text-sm text-gray-500 mb-2">
              {new Date(order.createdAt).toLocaleDateString('en-IN', {
                day: 'numeric', month: 'short', year: 'numeric',
                hour: '2-digit', minute: '2-digit',
              })}
            </p>
            <p className="text-sm text-gray-500 mb-3 truncate">📍 {order.deliveryAddress}</p>
            <div className="flex justify-between items-center border-t border-gray-50 pt-3">
              <span className="text-sm text-gray-400">{order.items?.length ?? 0} item(s)</span>
              <span className="font-bold text-gray-900">₹{order.totalAmount.toFixed(2)}</span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default Orders;
