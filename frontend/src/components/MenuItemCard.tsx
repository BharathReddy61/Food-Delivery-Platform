import type { MenuItem } from '../types';

interface MenuItemCardProps {
  item: MenuItem;
  onAddToCart: (menuItemId: number) => void;
  adding: boolean;
}

const MenuItemCard = ({ item, onAddToCart, adding }: MenuItemCardProps) => (
  <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-4 flex justify-between items-start gap-4">
    <div className="flex-1 min-w-0">
      <div className="flex items-center gap-2 mb-1">
        <h4 className="font-semibold text-gray-900">{item.name}</h4>
        {!item.available && (
          <span className="text-xs bg-gray-100 text-gray-500 px-2 py-0.5 rounded-full">Unavailable</span>
        )}
      </div>
      {item.description && (
        <p className="text-sm text-gray-500 mb-2 line-clamp-2">{item.description}</p>
      )}
      <p className="text-sm text-gray-400">{item.category}</p>
    </div>

    <div className="flex flex-col items-end gap-3 flex-shrink-0">
      <span className="font-bold text-gray-900">₹{item.price.toFixed(2)}</span>
      <button
        disabled={!item.available || adding}
        onClick={() => onAddToCart(item.id)}
        className="px-4 py-1.5 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-200 disabled:text-gray-400 text-white text-sm font-medium rounded-lg transition-colors"
      >
        {adding ? '...' : '+ Add'}
      </button>
    </div>
  </div>
);

export default MenuItemCard;
