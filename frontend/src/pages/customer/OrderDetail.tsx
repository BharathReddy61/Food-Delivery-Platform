import { useEffect, useState } from 'react';
import { useParams, Link, useSearchParams } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import StatusBadge from '../../components/StatusBadge';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Order } from '../../types';

const OrderDetail = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const [searchParams] = useSearchParams();
  const paymentSuccess = searchParams.get('payment') === 'success';

  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!orderId) return;
    orderService.getOrderById(Number(orderId))
      .then(setOrder)
      .catch(err => setError(err.response?.data?.message || 'Failed to load order.'))
      .finally(() => setLoading(false));
  }, [orderId]);

  if (loading) return <LoadingSpinner message="Loading order..." />;
  if (error) return <ErrorMessage message={error} />;
  if (!order) return null;

  return (
    <div className="max-w-2xl mx-auto">
      {/* Payment confirmed banner — shown only after backend-verified checkout */}
      {paymentSuccess && (
        <div className="bg-green-50 border border-green-200 rounded-xl p-4 mb-6 flex items-start gap-3">
          <span className="text-2xl">✅</span>
          <div>
            <p className="font-semibold text-green-800">Payment Confirmed!</p>
            <p className="text-sm text-green-700 mt-1">
              Your order has been placed and payment verified by our server. You'll receive updates as your order progresses.
            </p>
          </div>
        </div>
      )}

      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Order #{order.orderId}</h1>
        <StatusBadge status={order.status} />
      </div>

      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        {/* Order Items */}
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="font-semibold text-gray-700 mb-3">Items</h2>
          <div className="space-y-2">
            {order.items.map(item => (
              <div key={item.menuItemId} className="flex justify-between text-sm">
                <span className="text-gray-700">{item.menuItemName} × {item.quantity}</span>
                <span className="font-medium text-gray-900">₹{item.itemTotal.toFixed(2)}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Total — backend-authoritative, never recalculated */}
        <div className="px-6 py-4 bg-gray-50 flex justify-between items-center border-b border-gray-100">
          <span className="font-medium text-gray-700">Order Total</span>
          <span className="text-xl font-bold text-gray-900">₹{order.totalAmount.toFixed(2)}</span>
        </div>

        {/* Metadata */}
        <div className="px-6 py-4 space-y-2 text-sm text-gray-600">
          <p><span className="font-medium">Delivery Address:</span> {order.deliveryAddress}</p>
          <p><span className="font-medium">Placed:</span> {new Date(order.createdAt).toLocaleString('en-IN')}</p>
        </div>
      </div>

      <div className="mt-6 flex gap-3">
        <Link to="/orders" className="text-blue-600 hover:underline text-sm font-medium">
          ← Back to orders
        </Link>
      </div>
    </div>
  );
};

export default OrderDetail;
