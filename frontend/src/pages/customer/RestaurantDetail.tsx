import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { restaurantService } from '../../services/restaurantService';
import { cartService } from '../../services/cartService';
import MenuItemCard from '../../components/MenuItemCard';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Restaurant, MenuItem } from '../../types';

const RestaurantDetail = () => {
  const { id } = useParams<{ id: string }>();
  const restaurantId = Number(id);

  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [addingItemId, setAddingItemId] = useState<number | null>(null);
  const [cartFeedback, setCartFeedback] = useState('');

  useEffect(() => {
    if (!restaurantId) return;

    Promise.all([
      restaurantService.getById(restaurantId),
      restaurantService.getMenuByRestaurant(restaurantId),
    ])
      .then(([rest, menu]) => {
        setRestaurant(rest);
        setMenuItems(menu);
      })
      .catch(err => setError(err.response?.data?.message || 'Failed to load restaurant.'))
      .finally(() => setLoading(false));
  }, [restaurantId]);

  const handleAddToCart = async (menuItemId: number) => {
    setAddingItemId(menuItemId);
    setCartFeedback('');
    try {
      await cartService.addItem(menuItemId, 1);
      setCartFeedback('Item added to cart!');
    } catch (err: any) {
      setCartFeedback(err.response?.data?.message || 'Failed to add item.');
    } finally {
      setAddingItemId(null);
    }
  };

  if (loading) return <LoadingSpinner message="Loading restaurant..." />;
  if (error) return <ErrorMessage message={error} />;
  if (!restaurant) return null;

  // Group items by category
  const categories = [...new Set(menuItems.map(item => item.category))];

  return (
    <div>
      {/* Restaurant Header */}
      <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6 mb-8">
        <div className="flex justify-between items-start">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{restaurant.name}</h1>
            <p className="text-gray-500 mt-1">{restaurant.cuisineType} · {restaurant.city}</p>
            {restaurant.description && (
              <p className="text-gray-600 mt-2 text-sm max-w-xl">{restaurant.description}</p>
            )}
          </div>
          <div className="flex flex-col items-end gap-2">
            <span className={`text-sm px-3 py-1 rounded-full font-medium ${restaurant.open ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'}`}>
              {restaurant.open ? '● Open' : '● Closed'}
            </span>
            <span className="text-yellow-600 font-medium">★ {restaurant.rating > 0 ? restaurant.rating.toFixed(1) : 'New'}</span>
          </div>
        </div>
      </div>

      {/* Cart feedback toast */}
      {cartFeedback && (
        <div className={`mb-4 px-4 py-2 rounded-md text-sm font-medium ${cartFeedback.includes('added') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
          {cartFeedback}
        </div>
      )}

      {/* Menu by category */}
      {categories.length === 0 && (
        <p className="text-gray-500 text-center py-8">No menu items available.</p>
      )}

      {categories.map(category => {
        const items = menuItems.filter(item => item.category === category);
        return (
          <section key={category} className="mb-8">
            <h2 className="text-lg font-bold text-gray-700 mb-3 border-b border-gray-100 pb-2">{category}</h2>
            <div className="space-y-3">
              {items.map(item => (
                <MenuItemCard
                  key={item.id}
                  item={item}
                  onAddToCart={handleAddToCart}
                  adding={addingItemId === item.id}
                />
              ))}
            </div>
          </section>
        );
      })}
    </div>
  );
};

export default RestaurantDetail;
