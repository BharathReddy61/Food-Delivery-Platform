import { useEffect, useState, useCallback } from 'react';
import { deliveryService } from '../../services/deliveryService';
import DeliveryTaskCard from '../../components/delivery/DeliveryTaskCard';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Order } from '../../types';

type Tab = 'available' | 'active' | 'completed';

const DeliveryDashboard = () => {
  const [available, setAvailable] = useState<Order[]>([]);
  const [myDeliveries, setMyDeliveries] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [tab, setTab] = useState<Tab>('active');

  const fetchData = useCallback(async () => {
    try {
      const [avail, mine] = await Promise.all([
        deliveryService.getAvailableDeliveries(),
        deliveryService.getMyDeliveries()
      ]);
      setAvailable(avail);
      setMyDeliveries(mine);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load delivery data.');
    }
  }, []);

  useEffect(() => {
    fetchData().finally(() => setLoading(false));
  }, [fetchData]);

  const handleClaim = async (orderId: number) => {
    await deliveryService.assignOrder(orderId);
    await fetchData(); // Refresh all state from backend
  };

  const handleStatusUpdate = async (orderId: number, status: string) => {
    await deliveryService.updateDeliveryStatus(orderId, status);
    await fetchData(); // Refresh all state from backend
  };

  if (loading) return <LoadingSpinner message="Loading logistics dashboard..." />;

  const activeDeliveries = myDeliveries.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED');
  const completedDeliveries = myDeliveries.filter(o => o.status === 'DELIVERED');

  const getDisplayed = () => {
    if (tab === 'available') return available;
    if (tab === 'active') return activeDeliveries;
    return completedDeliveries;
  };

  const displayed = getDisplayed();

  const tabClass = (t: Tab) =>
    `px-4 py-2 rounded-lg text-sm font-semibold transition-colors ${
      tab === t ? 'bg-blue-600 text-white shadow-sm' : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-200'
    }`;

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Logistics Dashboard</h1>
      <p className="text-gray-500 mb-8">Claim orders and manage your delivery lifecycle</p>

      {error && <ErrorMessage message={error} />}

      {/* Tabs */}
      <div className="flex flex-wrap gap-3 mb-8">
        <button className={tabClass('available')} onClick={() => setTab('available')}>
          Available Tasks ({available.length})
        </button>
        <button className={tabClass('active')} onClick={() => setTab('active')}>
          My Active ({activeDeliveries.length})
        </button>
        <button className={tabClass('completed')} onClick={() => setTab('completed')}>
          History ({completedDeliveries.length})
        </button>
        
        <button 
          onClick={() => { setError(''); fetchData(); }}
          className="ml-auto px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-medium transition-colors"
        >
          ↻ Refresh
        </button>
      </div>

      {displayed.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-100 p-12 text-center">
          <p className="text-gray-500 mb-2">
            {tab === 'available' && "No orders are currently waiting for delivery."}
            {tab === 'active' && "You don't have any active deliveries. Claim one from Available Tasks!"}
            {tab === 'completed' && "You haven't completed any deliveries yet."}
          </p>
          {tab === 'active' && (
            <button 
              onClick={() => setTab('available')}
              className="text-blue-600 font-semibold hover:underline text-sm"
            >
              View available tasks →
            </button>
          )}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {displayed.map(order => (
            <DeliveryTaskCard
              key={order.orderId}
              order={order}
              isAvailable={tab === 'available'}
              onAction={tab === 'available' ? handleClaim : (id, status) => handleStatusUpdate(id, status!)}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default DeliveryDashboard;
