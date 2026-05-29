import api from '../api/axiosConfig';
import type { PaymentOrder } from '../types';

export const paymentService = {
  // Creates Razorpay order on backend — returns public metadata for popup
  initiatePayment: (orderId: number): Promise<PaymentOrder> =>
    api.post(`/api/payments/create/${orderId}`).then(r => r.data),

  // Sends Razorpay popup response to backend for cryptographic verification
  // Frontend NEVER marks payment as successful — backend is sole authority
  verifyPayment: (params: {
    razorpayOrderId: string;
    razorpayPaymentId: string;
    razorpaySignature: string;
  }): Promise<void> =>
    api.post('/api/payments/verify', params).then(() => undefined),
};
