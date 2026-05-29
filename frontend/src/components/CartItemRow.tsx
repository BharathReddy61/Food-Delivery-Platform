import type { CartItem } from '../types';

interface CartItemRowProps {
  item: CartItem;
  onRemove: (menuItemId: number) => void;
  onQuantityChange: (menuItemId: number, newQty: number) => void;
  updating: boolean;
}

const CartItemRow = ({ item, onRemove, onQuantityChange, updating }: CartItemRowProps) => (
  <div className="flex items-center gap-4 py-4 border-b border-gray-100 last:border-0">
    <div className="flex-1 min-w-0">
      <p className="font-semibold text-gray-900">{item.menuItemName}</p>
      <p className="text-sm text-gray-500">₹{item.itemPrice.toFixed(2)} each</p>
    </div>

    {/* Quantity stepper */}
    <div className="flex items-center gap-2">
      <button
        disabled={updating || item.quantity <= 1}
        onClick={() => onQuantityChange(item.menuItemId, item.quantity - 1)}
        className="w-7 h-7 flex items-center justify-center rounded-md bg-gray-100 hover:bg-gray-200 disabled:opacity-40 text-gray-700 font-bold transition-colors"
      >
        −
      </button>
      <span className="w-6 text-center font-medium text-gray-900">{item.quantity}</span>
      <button
        disabled={updating}
        onClick={() => onQuantityChange(item.menuItemId, item.quantity + 1)}
        className="w-7 h-7 flex items-center justify-center rounded-md bg-gray-100 hover:bg-gray-200 disabled:opacity-40 text-gray-700 font-bold transition-colors"
      >
        +
      </button>
    </div>

    {/* Item total — displayed from backend response, never recalculated */}
    <span className="font-bold text-gray-900 w-20 text-right">₹{item.itemTotal.toFixed(2)}</span>

    <button
      disabled={updating}
      onClick={() => onRemove(item.menuItemId)}
      className="text-red-400 hover:text-red-600 disabled:opacity-40 text-xl leading-none transition-colors"
      aria-label="Remove item"
    >
      ×
    </button>
  </div>
);

export default CartItemRow;
