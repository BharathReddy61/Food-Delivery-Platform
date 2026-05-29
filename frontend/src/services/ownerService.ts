import api from '../api/axiosConfig';
import type { OwnerDashboardData } from '../types';
import type { Order, MenuItem, Restaurant } from '../types';

// All requests automatically carry the JWT via Axios interceptor.
// Server validates RESTAURANT_OWNER role and ownership server-side.
export const ownerService = {
  // ── Dashboard ────────────────────────────────────────────────────────────
  getDashboardData: (): Promise<OwnerDashboardData> =>
    api.get('/api/owner/dashboard').then(r => r.data),

  // ── Restaurants ──────────────────────────────────────────────────────────
  getMyRestaurants: (): Promise<Restaurant[]> =>
    api.get('/api/owner/restaurants').then(r => r.data),

  updateRestaurant: (id: number, data: Partial<Restaurant>): Promise<Restaurant> =>
    api.put(`/api/owner/restaurants/${id}`, data).then(r => r.data),

  // ── Orders ───────────────────────────────────────────────────────────────
  getMyOrders: (): Promise<Order[]> =>
    api.get('/api/owner/orders').then(r => r.data),

  getOrderById: (orderId: number): Promise<Order> =>
    api.get(`/api/owner/orders/${orderId}`).then(r => r.data),

  updateOrderStatus: (orderId: number, status: string): Promise<Order> =>
    api.patch(`/api/owner/orders/${orderId}/status`, { status }).then(r => r.data),

  // ── Menu ─────────────────────────────────────────────────────────────────
  getMenuByRestaurant: (restaurantId: number): Promise<MenuItem[]> =>
    api.get(`/api/menu/restaurant/${restaurantId}`).then(r => r.data),

  createMenuItem: (restaurantId: number, data: Omit<MenuItem, 'id' | 'restaurantId' | 'restaurantName'>): Promise<MenuItem> =>
    api.post(`/api/owner/menu/${restaurantId}`, data).then(r => r.data),

  updateMenuItem: (itemId: number, data: Omit<MenuItem, 'id' | 'restaurantId' | 'restaurantName'>): Promise<MenuItem> =>
    api.put(`/api/owner/menu/${itemId}`, data).then(r => r.data),

  deleteMenuItem: (itemId: number): Promise<void> =>
    api.delete(`/api/owner/menu/${itemId}`).then(() => undefined),
};
