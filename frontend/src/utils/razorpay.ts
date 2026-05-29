import type { PaymentOrder, RazorpayPaymentResult } from '../types';

// ─────────────────────────────────────────────────────────────────────────────
// Razorpay script loader
// Loads the Razorpay checkout.js script dynamically once — safe to call multiple
// times; subsequent calls resolve immediately after the first load.
// ─────────────────────────────────────────────────────────────────────────────
export function loadRazorpayScript(): Promise<boolean> {
  return new Promise(resolve => {
    if (document.getElementById('razorpay-script')) {
      resolve(true);
      return;
    }
    const script = document.createElement('script');
    script.id = 'razorpay-script';
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// launchRazorpayCheckout
//
// Opens the Razorpay popup and resolves with the raw Razorpay payment response.
// IMPORTANT: This response is NOT proof of payment. It MUST be forwarded to
// the backend verify endpoint for cryptographic signature validation.
//
// Returns null if the user dismisses/cancels the popup.
// ─────────────────────────────────────────────────────────────────────────────
export function launchRazorpayCheckout(
  paymentOrder: PaymentOrder,
  userEmail: string,
): Promise<RazorpayPaymentResult | null> {
  return new Promise((resolve) => {
    const options = {
      key: paymentOrder.keyId,                         // public key from backend
      amount: Math.round(paymentOrder.amount * 100),   // paise — backend already validated
      currency: paymentOrder.currency,
      order_id: paymentOrder.razorpayOrderId,
      name: 'CraveDelivery',
      description: 'Food Order Payment',
      prefill: { email: userEmail },
      theme: { color: '#2563eb' },

      handler: (response: RazorpayPaymentResult) => {
        // Popup returned — this is raw Razorpay data, NOT a payment confirmation.
        // The caller must forward this to backend for verification.
        resolve(response);
      },

      modal: {
        ondismiss: () => {
          // User cancelled the popup — resolve null, do not mark as failed
          resolve(null);
        },
      },
    };

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const rzp = new (window as any).Razorpay(options);

    rzp.on('payment.failed', () => {
      // Razorpay emits this on network/bank failures — resolve null so caller
      // can show an error and allow retry. Backend webhook handles actual state.
      resolve(null);
    });

    rzp.open();
  });
}
