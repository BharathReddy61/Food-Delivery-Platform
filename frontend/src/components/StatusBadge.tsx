import type { OrderStatus } from '../types';

const STATUS_STYLES: Record<OrderStatus, string> = {
  PENDING:          'bg-yellow-100 text-yellow-800',
  ACCEPTED:         'bg-blue-100 text-blue-800',
  PREPARING:        'bg-orange-100 text-orange-800',
  OUT_FOR_DELIVERY: 'bg-purple-100 text-purple-800',
  DELIVERED:        'bg-green-100 text-green-800',
  CANCELLED:        'bg-red-100 text-red-800',
};

const STATUS_LABELS: Record<OrderStatus, string> = {
  PENDING:          'Pending',
  ACCEPTED:         'Accepted',
  PREPARING:        'Preparing',
  OUT_FOR_DELIVERY: 'Out for Delivery',
  DELIVERED:        'Delivered',
  CANCELLED:        'Cancelled',
};

const StatusBadge = ({ status }: { status: OrderStatus }) => (
  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${STATUS_STYLES[status] ?? 'bg-gray-100 text-gray-700'}`}>
    {STATUS_LABELS[status] ?? status}
  </span>
);

export default StatusBadge;
