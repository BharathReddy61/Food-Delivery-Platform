import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../../services/cartService';
import CartItemRow from '../../components/CartItemRow';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Cart } from '../../types';

const CartPage = () => {
  const navigate = useNavigate();

  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState('');

  // Single fetch function — always syncs from backend
  const fetchCart = useCallback(() => {
    return cartService.getCart()
      .then(setCart)
      .catch(err => setError(err.response?.data?.message || 'Failed to load cart.'));
  }, []);

  useEffect(() => {
    fetchCart().finally(() => setLoading(false));
  }, [fetchCart]);

  const handleRemove = async (menuItemId: number) => {
    setUpdating(true);
    setError('');
    try {
      const updated = await cartService.removeItem(menuItemId);
      setCart(updated); // always replace state from backend response
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to remove item.');
    } finally {
      setUpdating(false);
    }
  };

  const handleQuantityChange = async (menuItemId: number, newQty: number) => {
    if (newQty < 1) return;
    setUpdating(true);
    setError('');
    try {
      // Uses dedicated PATCH endpoint with replacement semantics
      const updated = await cartService.updateQuantity(menuItemId, newQty);
      setCart(updated); // always replace state from backend response
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update quantity.');
    } finally {
      setUpdating(false);
    }
  };


  if (loading) return <LoadingSpinner message="Loading your cart..." />;

  const isEmpty = !cart || !cart.items || cart.items.length === 0;

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Your Cart</h1>

      {error && <ErrorMessage message={error} />}

      {isEmpty ? (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-10 text-center">
          <p className="text-5xl mb-4">🛒</p>
          <p className="text-gray-600 text-lg font-medium mb-2">Your cart is empty</p>
          <p className="text-gray-400 text-sm mb-6">Add some items from a restaurant to get started</p>
          <button
            onClick={() => navigate('/restaurants')}
            className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium transition-colors"
          >
            Browse Restaurants
          </button>
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
          {/* Cart items */}
          <div className="px-6">
            {cart.items.map(item => (
              <CartItemRow
                key={item.menuItemId}
                item={item}
                onRemove={handleRemove}
                onQuantityChange={handleQuantityChange}
                updating={updating}
              />
            ))}
          </div>

          {/* Order total — displayed from backend, never recalculated locally */}
          <div className="bg-gray-50 px-6 py-4 flex justify-between items-center border-t border-gray-100">
            <span className="text-gray-600 font-medium">Order Total</span>
            <span className="text-2xl font-bold text-gray-900">₹{cart.totalAmount.toFixed(2)}</span>
          </div>

          {/* Checkout section — delivery address + pay moved to /checkout page */}
          <div className="px-6 py-5 border-t border-gray-100">
            <button
              onClick={() => navigate('/checkout')}
              disabled={updating}
              className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-semibold py-3 px-4 rounded-lg transition-colors"
            >
              Proceed to Checkout
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CartPage;
