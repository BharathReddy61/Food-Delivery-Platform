import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../../services/cartService';
import { orderService } from '../../services/orderService';
import { paymentService } from '../../services/paymentService';
import { loadRazorpayScript, launchRazorpayCheckout } from '../../utils/razorpay';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Cart, Order } from '../../types';

// ─────────────────────────────────────────────────────────────────────────────
// Payment state machine
// Frontend only renders based on this state — backend determines actual truth
// ─────────────────────────────────────────────────────────────────────────────
type PaymentStep =
  | 'idle'           // address entry
  | 'placing_order'  // POST /api/orders/checkout in progress
  | 'initiating'     // POST /api/payments/create/{orderId} in progress
  | 'popup_open'     // Razorpay popup visible
  | 'verifying'      // POST /api/payments/verify in progress
  | 'success'        // backend confirmed — redirect imminent
  | 'cancelled'      // user dismissed popup
  | 'error';         // backend returned error

const Checkout = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [cart, setCart] = useState<Cart | null>(null);
  const [loadingCart, setLoadingCart] = useState(true);
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [step, setStep] = useState<PaymentStep>('idle');
  const [errorMessage, setErrorMessage] = useState('');
  const [order, setOrder] = useState<Order | null>(null);

  useEffect(() => {
    // Preload Razorpay script while user fills address
    loadRazorpayScript();

    cartService.getCart()
      .then(setCart)
      .catch(() => setErrorMessage('Failed to load cart.'))
      .finally(() => setLoadingCart(false));
  }, []);

  const handlePayNow = async () => {
    if (!deliveryAddress.trim()) {
      setErrorMessage('Please enter a delivery address.');
      return;
    }
    setErrorMessage('');

    // ── Step 1: Create immutable order on backend ──────────────────────────
    setStep('placing_order');
    let createdOrder: Order;
    try {
      createdOrder = await orderService.checkout(deliveryAddress);
      setOrder(createdOrder);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to place order.');
      setStep('error');
      return;
    }

    // ── Step 2: Create Razorpay payment order on backend ───────────────────
    setStep('initiating');
    let paymentOrder;
    try {
      paymentOrder = await paymentService.initiatePayment(createdOrder.orderId);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to initiate payment.');
      setStep('error');
      return;
    }

    // ── Step 3: Launch Razorpay popup ──────────────────────────────────────
    setStep('popup_open');
    const razorpayResult = await launchRazorpayCheckout(
      paymentOrder,
      user?.email ?? '',
    );

    if (!razorpayResult) {
      // User cancelled or payment failed at Razorpay level
      // Order exists on backend in PENDING state — user can retry
      setStep('cancelled');
      return;
    }

    // ── Step 4: Backend cryptographic verification ─────────────────────────
    // razorpayResult is NOT trusted as proof of payment here.
    // Backend will validate the signature and mark payment state.
    setStep('verifying');
    try {
      await paymentService.verifyPayment({
        razorpayOrderId: razorpayResult.razorpay_order_id,
        razorpayPaymentId: razorpayResult.razorpay_payment_id,
        razorpaySignature: razorpayResult.razorpay_signature,
      });
      // Backend confirmed payment — now safe to navigate
      setStep('success');
      navigate(`/orders/${createdOrder.orderId}?payment=success`);
    } catch (err: any) {
      setErrorMessage(
        err.response?.data?.message ||
        'Payment verification failed. If money was debited, contact support.'
      );
      setStep('error');
    }
  };

  if (loadingCart) return <LoadingSpinner message="Loading your cart..." />;

  const isEmpty = !cart || !cart.items || cart.items.length === 0;

  const isProcessing =
    step === 'placing_order' ||
    step === 'initiating' ||
    step === 'popup_open' ||
    step === 'verifying' ||
    step === 'success';

  const stepLabel: Record<PaymentStep, string> = {
    idle:          'Pay Now',
    placing_order: 'Placing Order...',
    initiating:    'Initialising Payment...',
    popup_open:    'Complete Payment in Popup...',
    verifying:     'Verifying Payment...',
    success:       'Payment Confirmed!',
    cancelled:     'Pay Now',
    error:         'Pay Now',
  };

  return (
    <div className="max-w-lg mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Checkout</h1>

      {isEmpty ? (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-10 text-center">
          <p className="text-5xl mb-4">🛒</p>
          <p className="text-gray-600 mb-4">Your cart is empty.</p>
          <button
            onClick={() => navigate('/restaurants')}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors"
          >
            Browse Restaurants
          </button>
        </div>
      ) : (
        <div className="space-y-6">
          {/* Order Summary — all values from backend */}
          <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-100">
              <h2 className="font-semibold text-gray-700">Order Summary</h2>
            </div>
            <div className="px-6 py-4 space-y-2">
              {cart.items.map(item => (
                <div key={item.menuItemId} className="flex justify-between text-sm">
                  <span className="text-gray-700">{item.menuItemName} × {item.quantity}</span>
                  <span className="font-medium text-gray-900">₹{item.itemTotal.toFixed(2)}</span>
                </div>
              ))}
            </div>
            {/* Total — backend-authoritative, displayed only */}
            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-between items-center">
              <span className="font-semibold text-gray-700">Total</span>
              <span className="text-xl font-bold text-gray-900">₹{cart.totalAmount.toFixed(2)}</span>
            </div>
          </div>

          {/* Delivery Address */}
          <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6">
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Delivery Address
            </label>
            <textarea
              value={deliveryAddress}
              onChange={e => setDeliveryAddress(e.target.value)}
              disabled={isProcessing}
              rows={3}
              placeholder="Enter your full delivery address..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none disabled:bg-gray-50"
            />
          </div>

          {/* Status messages */}
          {errorMessage && <ErrorMessage message={errorMessage} />}

          {step === 'cancelled' && (
            <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded-md text-sm">
              Payment was cancelled. Your order is saved — you can try paying again.
            </div>
          )}

          {step === 'verifying' && (
            <div className="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-3 rounded-md text-sm">
              Verifying payment with server... Please do not close this tab.
            </div>
          )}

          {/* Pay Button */}
          <button
            onClick={handlePayNow}
            disabled={isProcessing}
            className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-bold py-4 px-6 rounded-xl text-lg transition-colors shadow-sm"
          >
            {isProcessing && (
              <span className="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2 align-middle" />
            )}
            {stepLabel[step]}
          </button>

          <p className="text-xs text-gray-400 text-center">
            Payments are processed securely. Your total is verified server-side.
          </p>
        </div>
      )}
    </div>
  );
};

export default Checkout;
