package com.fooddelivery.service;

import com.fooddelivery.entity.Order;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssignOrder_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PREPARING);
        order.setDeliveryPartnerId(null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        deliveryService.assignOrder(1L, 500L);

        assertEquals(500L, order.getDeliveryPartnerId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testAssignOrder_AlreadyAssigned_ShouldFail() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PREPARING);
        order.setDeliveryPartnerId(600L); // Already assigned to 600

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> deliveryService.assignOrder(1L, 500L));
    }

    @Test
    void testUpdateStatus_UnauthorizedPartner_ShouldFail() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setDeliveryPartnerId(500L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Partner 700 tries to update order owned by 500
        assertThrows(BusinessException.class, () -> 
            deliveryService.updateDeliveryStatus(1L, OrderStatus.DELIVERED, 700L)
        );
    }

    @Test
    void testUpdateStatus_InvalidTransition_ShouldFail() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PREPARING);
        order.setDeliveryPartnerId(500L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Attempting to skip states (PREPARING -> DELIVERED)
        assertThrows(BusinessException.class, () -> 
            deliveryService.updateDeliveryStatus(1L, OrderStatus.DELIVERED, 500L)
        );
    }
}
