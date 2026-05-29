import { useState, useEffect } from 'react';
import type { MenuItemFormData } from '../../types';

interface MenuItemFormProps {
  initialData?: Partial<MenuItemFormData>;
  onSubmit: (data: MenuItemFormData) => Promise<void>;
  onCancel: () => void;
  submitLabel: string;
}

const EMPTY_FORM: MenuItemFormData = {
  name: '', description: '', price: 0, category: '', imageUrl: '', available: true,
};

const MenuItemForm = ({ initialData, onSubmit, onCancel, submitLabel }: MenuItemFormProps) => {
  const [form, setForm] = useState<MenuItemFormData>({ ...EMPTY_FORM, ...initialData });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    setForm({ ...EMPTY_FORM, ...initialData });
  }, [initialData]);

  const set = (field: keyof MenuItemFormData, value: string | number | boolean) =>
    setForm(prev => ({ ...prev, [field]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.name || !form.category || form.price <= 0) {
      setError('Name, category, and a positive price are required.');
      return;
    }
    setSubmitting(true);
    setError('');
    try {
      await onSubmit(form);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save menu item.');
    } finally {
      setSubmitting(false);
    }
  };

  const inputClass = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500";

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && <p className="text-red-600 text-sm bg-red-50 p-3 rounded-lg">{error}</p>}

      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2">
          <label className="block text-sm font-medium text-gray-700 mb-1">Item Name *</label>
          <input type="text" value={form.name} onChange={e => set('name', e.target.value)} className={inputClass} required />
        </div>
        <div className="col-span-2">
          <label className="block text-sm font-medium text-gray-700 mb-1">Description *</label>
          <textarea value={form.description} onChange={e => set('description', e.target.value)} rows={2} className={inputClass + ' resize-none'} />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Price (₹) *</label>
          <input type="number" min="0.01" step="0.01" value={form.price} onChange={e => set('price', parseFloat(e.target.value))} className={inputClass} required />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Category *</label>
          <input type="text" value={form.category} onChange={e => set('category', e.target.value)} className={inputClass} required />
        </div>
        <div className="col-span-2">
          <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
          <input type="text" value={form.imageUrl} onChange={e => set('imageUrl', e.target.value)} className={inputClass} placeholder="https://..." />
        </div>
        <div className="col-span-2 flex items-center gap-3">
          <input
            type="checkbox"
            id="available"
            checked={form.available}
            onChange={e => set('available', e.target.checked)}
            className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
          />
          <label htmlFor="available" className="text-sm font-medium text-gray-700">Available for ordering</label>
        </div>
      </div>

      <div className="flex gap-3 pt-2">
        <button
          type="submit"
          disabled={submitting}
          className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-medium px-6 py-2 rounded-lg text-sm transition-colors"
        >
          {submitting ? 'Saving...' : submitLabel}
        </button>
        <button type="button" onClick={onCancel} className="bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium px-6 py-2 rounded-lg text-sm transition-colors">
          Cancel
        </button>
      </div>
    </form>
  );
};

export default MenuItemForm;
