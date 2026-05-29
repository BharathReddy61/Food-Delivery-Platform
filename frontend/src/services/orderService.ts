import api from '../api/axiosConfig';
import type { Order } from '../types';

export const orderService = {
  getUserOrders: (): Promise<Order[]> =>
    api.get('/api/orders').then(r => r.data),

  getOrderById: (orderId: number): Promise<Order> =>
    api.get(`/api/orders/${orderId}`).then(r => r.data),

  checkout: (deliveryAddress: string): Promise<Order> =>
    api.post('/api/orders/checkout', { deliveryAddress }).then(r => r.data),
};
