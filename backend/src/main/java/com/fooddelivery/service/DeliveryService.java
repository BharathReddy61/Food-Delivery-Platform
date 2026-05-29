package com.fooddelivery.service;

import com.fooddelivery.dto.OrderResponseDto;
import com.fooddelivery.entity.Order;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeliveryService {

    private final OrderRepository orderRepository;
    private final OrderService orderService; // To reuse the mapping logic
    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    public DeliveryService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /**
     * Fetch orders that are ready for assignment but have no partner yet.
     * Orders in ACCEPTED or PREPARING status are eligible.
     */
    public List<OrderResponseDto> getAvailableOrders() {
        List<OrderStatus> eligibleStatuses = List.of(OrderStatus.ACCEPTED, OrderStatus.PREPARING);
        return orderRepository.findAvailableOrders(eligibleStatuses).stream()
                .map(orderService::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetch all orders currently assigned to the partner.
     */
    public List<OrderResponseDto> getPartnerDeliveries(Long partnerId) {
        return orderRepository.findByDeliveryPartnerId(partnerId).stream()
                .map(orderService::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Assign an order to a delivery partner.
     * Implements strict assignment integrity:
     * 1. Validates order existence
     * 2. Validates order is not already assigned
     * 3. Validates order status is eligible
     * 4. Updates deliveryPartnerId (protected by @Version optimistic locking)
     */
    @Transactional
    public OrderResponseDto assignOrder(Long orderId, Long partnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getDeliveryPartnerId() != null) {
            throw new BusinessException("Order is already assigned to another partner.");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.PREPARING) {
            throw new BusinessException("Order is not in a state eligible for assignment.");
        }

        order.setDeliveryPartnerId(partnerId);
        log.info("Order {} successfully assigned to partner {}", orderId, partnerId);
        // We don't automatically change status here; status changes are explicit (e.g.
        // PICKED_UP)
        return orderService.mapToResponseDto(orderRepository.save(order));
    }

    /**
     * Update order status by delivery partner.
     * Implements strict ownership and workflow validation:
     * 1. Validates partner ownership
     * 2. Validates allowed transition
     * 3. Prevents terminal state manipulation by unauthorized parties
     */
    @Transactional
    public OrderResponseDto updateDeliveryStatus(Long orderId, OrderStatus newStatus, Long partnerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!partnerId.equals(order.getDeliveryPartnerId())) {
            log.warn("Unauthorized status update attempt for order {} by partner {}. Assigned partner is {}",
                    orderId, partnerId, order.getDeliveryPartnerId());
            throw new BusinessException("You are not authorized to update this delivery.");
        }

        // Validate transition using backend state machine
        if (!order.getStatus().getAllowedTransitions().contains(newStatus.name())) {
            log.warn("Invalid transition attempt for order {} from {} to {} by partner {}",
                    orderId, order.getStatus(), newStatus, partnerId);
            throw new BusinessException("Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }

        // Only allow delivery-specific transitions for partners
        List<OrderStatus> partnerAllowedStatuses = List.of(
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.ARRIVING,
                OrderStatus.DELIVERED);

        if (!partnerAllowedStatuses.contains(newStatus)) {
            log.warn("Partner {} attempted forbidden transition to {} for order {}", partnerId, newStatus, orderId);
            throw new BusinessException("Delivery partners cannot set status to " + newStatus);
        }

        order.setStatus(newStatus);
        log.info("Order {} status updated to {} by partner {}", orderId, newStatus, partnerId);
        return orderService.mapToResponseDto(orderRepository.save(order));
    }
}
