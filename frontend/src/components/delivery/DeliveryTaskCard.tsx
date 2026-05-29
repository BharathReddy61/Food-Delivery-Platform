import { useState } from 'react';
import StatusBadge from '../StatusBadge';
import type { Order, OrderStatus } from '../../types';

interface DeliveryTaskCardProps {
  order: Order;
  onAction: (orderId: number, status?: string) => Promise<void>;
  isAvailable?: boolean;
}

const DeliveryTaskCard = ({ order, onAction, isAvailable = false }: DeliveryTaskCardProps) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleAction = async (status?: string) => {
    setLoading(true);
    setError('');
    try {
      await onAction(order.orderId, status);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Action failed.');
    } finally {
      setLoading(false);
    }
  };

  const STATUS_LABELS: Record<string, string> = {
    OUT_FOR_DELIVERY: 'Confirm Pickup',
    ARRIVING:         'Mark Arriving',
    DELIVERED:        'Complete Delivery',
  };

  const STATUS_COLORS: Record<string, string> = {
    OUT_FOR_DELIVERY: 'bg-blue-600 hover:bg-blue-700',
    ARRIVING:         'bg-purple-600 hover:bg-purple-700',
    DELIVERED:        'bg-green-600 hover:bg-green-700',
  };

  return (
    <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
      <div className="flex justify-between items-start mb-3">
        <div>
          <span className="font-bold text-gray-900">Order #{order.orderId}</span>
          <p className="text-xs text-gray-400 mt-0.5">
            {new Date(order.createdAt).toLocaleString('en-IN', {
              day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit',
            })}
          </p>
        </div>
        <StatusBadge status={order.status as OrderStatus} />
      </div>

      <div className="space-y-2 mb-4">
        <p className="text-sm text-gray-600">
          <span className="font-medium text-gray-800">Delivery Address:</span>
          <br />
          {order.deliveryAddress}
        </p>
      </div>

      {/* Item Summary (Optional for partner, but helpful) */}
      <div className="text-xs text-gray-500 mb-4 bg-gray-50 p-2 rounded-lg">
        {order.items.length} items to deliver
      </div>

      <div className="flex justify-between items-center border-t border-gray-50 pt-3">
        <span className="font-bold text-gray-900">₹{order.totalAmount.toFixed(2)}</span>
        
        <div className="flex gap-2">
          {isAvailable ? (
            <button
              onClick={() => handleAction()}
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white text-xs px-4 py-2 rounded-lg font-semibold transition-colors disabled:opacity-50"
            >
              {loading ? 'Claiming...' : 'Claim Delivery'}
            </button>
          ) : (
            <>
              {order.allowedTransitions.map(status => (
                <button
                  key={status}
                  onClick={() => handleAction(status)}
                  disabled={loading}
                  className={`${STATUS_COLORS[status] || 'bg-gray-600'} text-white text-xs px-4 py-2 rounded-lg font-semibold transition-colors disabled:opacity-50`}
                >
                  {loading ? 'Updating...' : (STATUS_LABELS[status] || status)}
                </button>
              ))}
            </>
          )}
        </div>
      </div>

      {error && <p className="text-red-500 text-xs mt-2">{error}</p>}
    </div>
  );
};

export default DeliveryTaskCard;
