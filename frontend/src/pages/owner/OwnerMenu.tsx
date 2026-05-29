import { useEffect, useState } from 'react';
import { ownerService } from '../../services/ownerService';
import MenuItemForm from '../../components/owner/MenuItemForm';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Restaurant, MenuItem, MenuItemFormData } from '../../types';

type Mode = 'list' | 'create' | 'edit';

const OwnerMenu = () => {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [selectedRestaurantId, setSelectedRestaurantId] = useState<number | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [menuLoading, setMenuLoading] = useState(false);
  const [error, setError] = useState('');
  const [mode, setMode] = useState<Mode>('list');
  const [editingItem, setEditingItem] = useState<MenuItem | null>(null);
  const [mutationLoading, setMutationLoading] = useState(false);

  useEffect(() => {
    ownerService.getMyRestaurants()
      .then(rests => {
        setRestaurants(rests);
        if (rests.length > 0) setSelectedRestaurantId(rests[0].id);
      })
      .catch(err => setError(err.response?.data?.message || 'Failed to load restaurants.'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!selectedRestaurantId) return;
    setMenuLoading(true);
    ownerService.getMenuByRestaurant(selectedRestaurantId)
      .then(setMenuItems)
      .catch(err => setError(err.response?.data?.message || 'Failed to load menu.'))
      .finally(() => setMenuLoading(false));
  }, [selectedRestaurantId]);

  const handleCreate = async (data: MenuItemFormData) => {
    if (!selectedRestaurantId) return;
    setMutationLoading(true);
    try {
      await ownerService.createMenuItem(selectedRestaurantId, data);
      const updated = await ownerService.getMenuByRestaurant(selectedRestaurantId);
      setMenuItems(updated);
      setMode('list');
    } finally {
      setMutationLoading(false);
    }
  };

  const handleEdit = async (data: MenuItemFormData) => {
    if (!editingItem) return;
    setMutationLoading(true);
    try {
      await ownerService.updateMenuItem(editingItem.id, data);
      const updated = await ownerService.getMenuByRestaurant(selectedRestaurantId!);
      setMenuItems(updated);
      setMode('list');
      setEditingItem(null);
    } finally {
      setMutationLoading(false);
    }
  };

  const handleDelete = async (itemId: number) => {
    if (!confirm('Delete this menu item? This cannot be undone.')) return;
    setMutationLoading(true);
    setError('');
    try {
      await ownerService.deleteMenuItem(itemId);
      setMenuItems(prev => prev.filter(i => i.id !== itemId));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete item.');
    } finally {
      setMutationLoading(false);
    }
  };

  const handleToggleAvailability = async (item: MenuItem) => {
    setMutationLoading(true);
    setError('');
    try {
      const updated = await ownerService.updateMenuItem(item.id, {
        name: item.name,
        description: item.description,
        price: item.price,
        category: item.category,
        imageUrl: item.imageUrl ?? '',
        available: !item.available,
      });
      setMenuItems(prev => prev.map(i => i.id === item.id ? updated : i));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update availability.');
    } finally {
      setMutationLoading(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading menu..." />;

  return (
    <div>
      <div className="flex justify-between items-center mb-2">
        <h1 className="text-3xl font-bold text-gray-900">Menu Management</h1>
        {mode === 'list' && (
          <button
            onClick={() => { setMode('create'); setEditingItem(null); }}
            className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-4 py-2 rounded-lg transition-colors"
          >
            + Add Item
          </button>
        )}
      </div>
      <p className="text-gray-500 mb-6">Create, edit, and manage availability of your menu items</p>

      {error && <ErrorMessage message={error} />}

      {/* Restaurant selector */}
      {restaurants.length > 1 && (
        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-2">Viewing menu for:</label>
          <select
            value={selectedRestaurantId ?? ''}
            onChange={e => { setSelectedRestaurantId(Number(e.target.value)); setMode('list'); }}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {restaurants.map(r => (
              <option key={r.id} value={r.id}>{r.name}</option>
            ))}
          </select>
        </div>
      )}

      {/* Create / Edit Form */}
      {(mode === 'create' || mode === 'edit') && (
        <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-6 mb-6">
          <h2 className="font-bold text-gray-900 mb-4">{mode === 'create' ? 'Add New Item' : 'Edit Item'}</h2>
          <MenuItemForm
            initialData={editingItem ?? undefined}
            onSubmit={mode === 'create' ? handleCreate : handleEdit}
            onCancel={() => { setMode('list'); setEditingItem(null); }}
            submitLabel={mode === 'create' ? 'Create Item' : 'Save Changes'}
          />
        </div>
      )}

      {/* Menu list */}
      {menuLoading ? (
        <LoadingSpinner message="Loading menu items..." />
      ) : menuItems.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-100 p-10 text-center text-gray-500">
          No menu items yet. Add your first item above.
        </div>
      ) : (
        <div className="space-y-3">
          {menuItems.map(item => (
            <div key={item.id} className="bg-white rounded-xl border border-gray-100 shadow-sm p-4 flex justify-between items-center gap-4">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <span className="font-semibold text-gray-900">{item.name}</span>
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${item.available ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
                    {item.available ? 'Available' : 'Unavailable'}
                  </span>
                </div>
                <p className="text-sm text-gray-500">{item.category}</p>
              </div>
              <span className="font-bold text-gray-900 flex-shrink-0">₹{item.price.toFixed(2)}</span>
              <div className="flex gap-2 flex-shrink-0">
                <button
                  onClick={() => handleToggleAvailability(item)}
                  disabled={mutationLoading}
                  className="text-xs px-3 py-1.5 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-medium transition-colors disabled:opacity-50"
                >
                  {item.available ? 'Disable' : 'Enable'}
                </button>
                <button
                  onClick={() => { setEditingItem(item); setMode('edit'); }}
                  disabled={mutationLoading}
                  className="text-xs px-3 py-1.5 bg-blue-50 hover:bg-blue-100 text-blue-700 rounded-lg font-medium transition-colors disabled:opacity-50"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDelete(item.id)}
                  disabled={mutationLoading}
                  className="text-xs px-3 py-1.5 bg-red-50 hover:bg-red-100 text-red-600 rounded-lg font-medium transition-colors disabled:opacity-50"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default OwnerMenu;
