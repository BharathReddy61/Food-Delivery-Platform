import api from '../api/axiosConfig';
import type { Cart } from '../types';

export const cartService = {
  getCart: (): Promise<Cart> =>
    api.get('/api/cart').then(r => r.data),

  addItem: (menuItemId: number, quantity: number): Promise<Cart> =>
    api.post('/api/cart/add', { menuItemId, quantity }).then(r => r.data),

  // REPLACEMENT semantics — sets absolute quantity, not additive
  updateQuantity: (menuItemId: number, quantity: number): Promise<Cart> =>
    api.patch(`/api/cart/items/${menuItemId}/quantity`, { quantity }).then(r => r.data),

  removeItem: (menuItemId: number): Promise<Cart> =>
    api.delete(`/api/cart/item/${menuItemId}`).then(r => r.data),
};
