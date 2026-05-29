import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ownerService } from '../../services/ownerService';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import type { Restaurant } from '../../types';

const OwnerRestaurant = () => {
  const { id } = useParams<{ id: string }>();
  const restaurantId = Number(id);

  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form state — initialised from backend response
  const [form, setForm] = useState({
    name: '', description: '', cuisineType: '', address: '', city: '', imageUrl: '',
  });
  const [isOpen, setIsOpen] = useState(true);

  useEffect(() => {
    ownerService.getMyRestaurants()
      .then(rests => {
        const found = rests.find(r => r.id === restaurantId);
        if (!found) { setError('Restaurant not found or you do not own it.'); return; }
        setRestaurant(found);
        setForm({
          name: found.name,
          description: found.description ?? '',
          cuisineType: found.cuisineType,
          address: found.address,
          city: found.city,
          imageUrl: found.imageUrl ?? '',
        });
        setIsOpen(found.open);
      })
      .catch(err => setError(err.response?.data?.message || 'Failed to load restaurant.'))
      .finally(() => setLoading(false));
  }, [restaurantId]);

  const set = (field: keyof typeof form, value: string) =>
    setForm(prev => ({ ...prev, [field]: value }));

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!restaurant) return;
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      const updated = await ownerService.updateRestaurant(restaurant.id, {
        ...form,
        open: isOpen,
        ownerId: restaurant.ownerId,
      });
      setRestaurant(updated);
      setSuccess('Restaurant details updated successfully.');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save changes.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading restaurant details..." />;
  if (!restaurant && error) return (
    <div>
      <ErrorMessage message={error} />
      <Link to="/owner/dashboard" className="text-blue-600 hover:underline text-sm mt-4 block">← Back to Dashboard</Link>
    </div>
  );

  const inputClass = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500";

  return (
    <div className="max-w-2xl mx-auto">
      <div className="flex items-center gap-3 mb-6">
        <Link to="/owner/dashboard" className="text-gray-400 hover:text-gray-600 text-sm">← Dashboard</Link>
        <span className="text-gray-300">/</span>
        <h1 className="text-2xl font-bold text-gray-900">Edit Restaurant</h1>
      </div>

      {error && <ErrorMessage message={error} />}
      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-md text-sm mb-4">{success}</div>
      )}

      <form onSubmit={handleSave} className="bg-white rounded-xl border border-gray-100 shadow-sm p-6 space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Restaurant Name *</label>
          <input type="text" value={form.name} onChange={e => set('name', e.target.value)} className={inputClass} required disabled={saving} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <textarea value={form.description} onChange={e => set('description', e.target.value)} rows={2} className={inputClass + ' resize-none'} disabled={saving} />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Cuisine Type *</label>
            <input type="text" value={form.cuisineType} onChange={e => set('cuisineType', e.target.value)} className={inputClass} required disabled={saving} />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">City *</label>
            <input type="text" value={form.city} onChange={e => set('city', e.target.value)} className={inputClass} required disabled={saving} />
          </div>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Address *</label>
          <input type="text" value={form.address} onChange={e => set('address', e.target.value)} className={inputClass} required disabled={saving} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
          <input type="text" value={form.imageUrl} onChange={e => set('imageUrl', e.target.value)} className={inputClass} placeholder="https://..." disabled={saving} />
        </div>

        {/* Open/Closed toggle */}
        <div className="flex items-center gap-3 pt-2 pb-1 border-t border-gray-100">
          <input
            type="checkbox"
            id="isOpen"
            checked={isOpen}
            onChange={e => setIsOpen(e.target.checked)}
            className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 disabled:opacity-50"
            disabled={saving}
          />
          <label htmlFor="isOpen" className="text-sm font-medium text-gray-700">Restaurant is currently open</label>
        </div>

        <button
          type="submit"
          disabled={saving}
          className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-semibold py-3 rounded-lg transition-colors"
        >
          {saving ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  );
};

export default OwnerRestaurant;
