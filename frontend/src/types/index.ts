// ─────────────────────────────────────────────────────────────────────────────
// Shared TypeScript interfaces — mirrors backend DTO contracts exactly.
// DO NOT add frontend-only calculated fields here.
// ─────────────────────────────────────────────────────────────────────────────

export interface Restaurant {
  id: number;
  name: string;
  description: string;
  cuisineType: string;
  address: string;
  city: string;
  imageUrl: string | null;
  rating: number;
  open: boolean;
  ownerId: number;
}

export interface MenuItem {
  id: number;
  name: string;
  description: string;
  price: number;
  available: boolean;
  category: string;
  imageUrl: string | null;
  restaurantId: number;
  restaurantName: string;
}

export interface CartItem {
  menuItemId: number;
  menuItemName: string;
  quantity: number;
  itemPrice: number;
  itemTotal: number;
}

export interface Cart {
  cartId: number;
  totalAmount: number;
  items: CartItem[];
}

export interface OrderItem {
  menuItemId: number;
  menuItemName: string;
  quantity: number;
  itemPrice: number;
  itemTotal: number;
}

export type OrderStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'PREPARING'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'CANCELLED';

export interface Order {
  orderId: number;
  restaurantId: number;
  totalAmount: number;
  deliveryAddress: string;
  status: OrderStatus;
  createdAt: string;
  items: OrderItem[];
  allowedTransitions: string[];
}

export interface OwnerDashboardData {
  activeOrderCount: number;
  completedOrderCount: number;
  cancelledOrderCount: number;
  totalRevenue: number;
  recentOrders: Order[];
  restaurants: Restaurant[];
}

// Backend: CreatePaymentResponseDto
export interface PaymentOrder {
  razorpayOrderId: string;
  amount: number;       // always in INR (full rupees) — backend converts to paise for Razorpay
  currency: string;
  keyId: string;        // public key only — never the secret
}

// Razorpay popup return value — must be sent to backend for verification
// Frontend NEVER treats this as proof of payment
export interface RazorpayPaymentResult {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
}

// Backend: ApiError
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

// Used for create/edit menu item forms — maps to MenuItemRequestDto
export interface MenuItemFormData {
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  available: boolean;
}
