import { useEffect, useState, useCallback } from 'react';
import { ownerService } from '../../services/ownerService';
import OrderQueueCard from '../../components/owner/OrderQueueCard';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Order } from '../../types';

type Tab = 'active' | 'completed';

const OwnerOrders = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [tab, setTab] = useState<Tab>('active');

  const fetchOrders = useCallback(() => {
    return ownerService.getMyOrders()
      .then(setOrders)
      .catch(err => setError(err.response?.data?.message || 'Failed to load orders.'));
  }, []);

  useEffect(() => {
    fetchOrders().finally(() => setLoading(false));
  }, [fetchOrders]);

  const handleStatusUpdate = async (orderId: number, newStatus: string) => {
    await ownerService.updateOrderStatus(orderId, newStatus);
    // Refresh entire order list from backend — no local state mutation
    await fetchOrders();
  };

  if (loading) return <LoadingSpinner message="Loading orders..." />;

  const TERMINAL = ['DELIVERED', 'CANCELLED'];
  const activeOrders = orders.filter(o => !TERMINAL.includes(o.status));
  const completedOrders = orders.filter(o => TERMINAL.includes(o.status));
  const displayed = tab === 'active' ? activeOrders : completedOrders;

  const tabClass = (t: Tab) =>
    `px-4 py-2 rounded-lg text-sm font-semibold transition-colors ${
      tab === t ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-200'
    }`;

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Order Queue</h1>
      <p className="text-gray-500 mb-6">Manage and update incoming orders from your restaurants</p>

      {error && <ErrorMessage message={error} />}

      {/* Tabs */}
      <div className="flex gap-3 mb-6">
        <button className={tabClass('active')} onClick={() => setTab('active')}>
          Active ({activeOrders.length})
        </button>
        <button className={tabClass('completed')} onClick={() => setTab('completed')}>
          Completed ({completedOrders.length})
        </button>
        <button
          onClick={() => { setError(''); fetchOrders(); }}
          className="ml-auto px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-medium transition-colors"
        >
          ↻ Refresh
        </button>
      </div>

      {displayed.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-100 p-10 text-center text-gray-500">
          {tab === 'active' ? 'No active orders right now.' : 'No completed orders yet.'}
        </div>
      ) : (
        <div className="space-y-4">
          {displayed.map(order => (
            <OrderQueueCard
              key={order.orderId}
              order={order}
              onStatusUpdate={handleStatusUpdate}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default OwnerOrders;
