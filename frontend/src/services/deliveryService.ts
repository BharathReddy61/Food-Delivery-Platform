import api from '../api/axiosConfig';
import type { Order } from '../types';

/**
 * Frontend service for logistics operations.
 * All requests carry JWT for server-side identity resolution and RBAC enforcement.
 */
export const deliveryService = {
  // ── Available Queue ──────────────────────────────────────────────────────
  /**
   * Fetch orders ready for assignment (ACCEPTED or PREPARING status, no partner).
   */
  getAvailableDeliveries: (): Promise<Order[]> =>
    api.get('/api/delivery/available').then(r => r.data),

  // ── Partner Deliveries ───────────────────────────────────────────────────
  /**
   * Fetch all orders currently assigned to the authenticated partner.
   */
  getMyDeliveries: (): Promise<Order[]> =>
    api.get('/api/delivery/my-deliveries').then(r => r.data),

  // ── Mutations ────────────────────────────────────────────────────────────
  /**
   * Claim an order for the authenticated partner.
   * Server validates assignment integrity and optimistic locking.
   */
  assignOrder: (orderId: number): Promise<Order> =>
    api.post(`/api/delivery/assign/${orderId}`).then(r => r.data),

  /**
   * Update delivery status (PICKED_UP, ARRIVING, DELIVERED).
   * Server validates partner ownership and state machine transitions.
   */
  updateDeliveryStatus: (orderId: number, status: string): Promise<Order> =>
    api.patch(`/api/delivery/${orderId}/status`, { status }).then(r => r.data),
};
