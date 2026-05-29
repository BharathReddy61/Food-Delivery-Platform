package com.fooddelivery.service;

import com.fooddelivery.dto.CreatePaymentResponseDto;
import com.fooddelivery.dto.VerifyPaymentRequestDto;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.Payment;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.enums.PaymentStatus;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    // NON-transactional to avoid blocking DB connection during HTTP call
    public CreatePaymentResponseDto createPaymentOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("You do not have permission to pay for this order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Cannot initialize payment for order in status: " + order.getStatus());
        }

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            // Razorpay expects amount in paise (multiply by 100)
            int amountInPaise = (int) Math.round(order.getTotalAmount() * 100);
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + orderId);

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);
            String rpOrderId = razorpayOrder.get("id");

            log.info("Initialized Razorpay payment for orderId: {}, rpOrderId: {}", orderId, rpOrderId);
            savePendingPayment(orderId, rpOrderId, order.getTotalAmount(), "INR");

            return new CreatePaymentResponseDto(rpOrderId, order.getTotalAmount(), "INR", razorpayKeyId);

        } catch (Exception e) {
            log.error("Failed to initialize payment for orderId: {}. Error: {}", orderId, e.getMessage());
            throw new BusinessException("Failed to initialize payment: " + e.getMessage());
        }
    }

    @Transactional
    public void savePendingPayment(Long orderId, String razorpayOrderId, Double amount, String currency) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
    }

    @Transactional
    public void verifyPayment(VerifyPaymentRequestDto dto) {
        Payment payment = paymentRepository.findByRazorpayOrderId(dto.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        // Idempotency check: successful payments must never be processed twice
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", dto.getRazorpayOrderId());
            options.put("razorpay_payment_id", dto.getRazorpayPaymentId());
            options.put("razorpay_signature", dto.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (isValid) {
                payment.setRazorpayPaymentId(dto.getRazorpayPaymentId());
                payment.setRazorpaySignature(dto.getRazorpaySignature());
                payment.setPaymentStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                log.info("Payment verified successfully for rpOrderId: {}", dto.getRazorpayOrderId());
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                log.warn("Invalid payment signature for rpOrderId: {}", dto.getRazorpayOrderId());
                throw new BusinessException("Invalid payment signature");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Payment verification failed: " + e.getMessage());
        }
    }

    @Transactional
    public void handleWebhookEvent(String payload, String signature) {
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!isValid) {
                throw new BusinessException("Invalid webhook signature");
            }

            JSONObject jsonPayload = new JSONObject(payload);
            String event = jsonPayload.getString("event");

            if ("payment.captured".equals(event)) {
                JSONObject paymentEntity = jsonPayload.getJSONObject("payload").getJSONObject("payment")
                        .getJSONObject("entity");
                String rpOrderId = paymentEntity.getString("order_id");
                String rpPaymentId = paymentEntity.getString("id");

                Payment payment = paymentRepository.findByRazorpayOrderId(rpOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

                // Idempotency: safely ignore if already PAID
                if (payment.getPaymentStatus() == PaymentStatus.PAID) {
                    return;
                }

                payment.setRazorpayPaymentId(rpPaymentId);
                payment.setPaymentStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                log.info("Payment captured via webhook for rpOrderId: {}", rpOrderId);

            } else if ("payment.failed".equals(event)) {
                JSONObject paymentEntity = jsonPayload.getJSONObject("payload").getJSONObject("payment")
                        .getJSONObject("entity");
                String rpOrderId = paymentEntity.getString("order_id");

                Payment payment = paymentRepository.findByRazorpayOrderId(rpOrderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

                // Never overwrite successful payment destructively
                if (payment.getPaymentStatus() == PaymentStatus.PAID) {
                    return;
                }

                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                log.warn("Payment failed via webhook for rpOrderId: {}", rpOrderId);
            }
        } catch (Exception e) {
            log.error("Webhook processing failed. Error: {}", e.getMessage());
            throw new BusinessException("Webhook processing failed: " + e.getMessage());
        }
    }
}
