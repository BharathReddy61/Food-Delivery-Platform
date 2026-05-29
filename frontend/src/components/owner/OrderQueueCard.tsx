import { useState } from 'react';
import StatusBadge from '../StatusBadge';
import type { Order, OrderStatus } from '../../types';

interface OrderQueueCardProps {
  order: Order;
  onStatusUpdate: (orderId: number, newStatus: string) => Promise<void>;
}

const OrderQueueCard = ({ order, onStatusUpdate }: OrderQueueCardProps) => {
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState('');

  const availableTransitions = order.allowedTransitions ?? [];

  const handleTransition = async (newStatus: string) => {
    setUpdating(true);
    setError('');
    try {
      await onStatusUpdate(order.orderId, newStatus);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Status update failed.');
    } finally {
      setUpdating(false);
    }
  };

  const STATUS_BUTTON_STYLES: Record<string, string> = {
    ACCEPTED:         'bg-blue-600 hover:bg-blue-700 text-white',
    PREPARING:        'bg-orange-500 hover:bg-orange-600 text-white',
    OUT_FOR_DELIVERY: 'bg-purple-600 hover:bg-purple-700 text-white',
    DELIVERED:        'bg-green-600 hover:bg-green-700 text-white',
    CANCELLED:        'bg-red-500 hover:bg-red-600 text-white',
  };

  const STATUS_LABELS: Record<string, string> = {
    ACCEPTED:         'Accept Order',
    PREPARING:        'Mark Preparing',
    OUT_FOR_DELIVERY: 'Out for Delivery',
    DELIVERED:        'Mark Delivered',
    CANCELLED:        'Cancel Order',
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

      <p className="text-sm text-gray-600 mb-2 truncate">📍 {order.deliveryAddress}</p>

      <div className="text-sm text-gray-500 mb-3 space-y-0.5">
        {order.items.map(item => (
          <p key={item.menuItemId}>{item.menuItemName} × {item.quantity}</p>
        ))}
      </div>

      <div className="flex justify-between items-center border-t border-gray-50 pt-3">
        <span className="font-bold text-gray-900">₹{order.totalAmount.toFixed(2)}</span>
        <div className="flex gap-2 flex-wrap justify-end">
          {availableTransitions.map(status => (
            <button
              key={status}
              disabled={updating}
              onClick={() => handleTransition(status)}
              className={`text-xs px-3 py-1.5 rounded-lg font-semibold transition-colors disabled:opacity-50 ${STATUS_BUTTON_STYLES[status] ?? 'bg-gray-200 text-gray-700'}`}
            >
              {STATUS_LABELS[status] ?? status}
            </button>
          ))}
        </div>
      </div>

      {error && <p className="text-red-500 text-xs mt-2">{error}</p>}
    </div>
  );
};

export default OrderQueueCard;
