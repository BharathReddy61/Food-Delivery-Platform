package com.fooddelivery.service;

import com.fooddelivery.entity.Order;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidStatusTransition_PendingToAccepted() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setRestaurantId(10L);

        com.fooddelivery.entity.Restaurant restaurant = new com.fooddelivery.entity.Restaurant();
        restaurant.setId(10L);
        restaurant.setOwnerId(100L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        orderService.updateOrderStatus(1L, OrderStatus.ACCEPTED, 100L, "RESTAURANT_OWNER");

        assertEquals(OrderStatus.ACCEPTED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testInvalidStatusTransition_DeliveredToPreparing() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.DELIVERED);
        order.setRestaurantId(10L);

        com.fooddelivery.entity.Restaurant restaurant = new com.fooddelivery.entity.Restaurant();
        restaurant.setId(10L);
        restaurant.setOwnerId(100L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));

        assertThrows(BusinessException.class, () -> 
            orderService.updateOrderStatus(1L, OrderStatus.PREPARING, 100L, "RESTAURANT_OWNER")
        );
    }

    @Test
    void testUnauthorizedOwnerUpdate() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setRestaurantId(10L);

        com.fooddelivery.entity.Restaurant restaurant = new com.fooddelivery.entity.Restaurant();
        restaurant.setId(10L);
        restaurant.setOwnerId(100L); // True owner is 100

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));

        // User 200 tries to update
        assertThrows(BusinessException.class, () -> 
            orderService.updateOrderStatus(1L, OrderStatus.ACCEPTED, 200L, "RESTAURANT_OWNER")
        );
    }

    @Test
    void testWorkflowIntegrity_CancelledToOutForDelivery() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);
        order.setRestaurantId(10L);

        com.fooddelivery.entity.Restaurant restaurant = new com.fooddelivery.entity.Restaurant();
        restaurant.setId(10L);
        restaurant.setOwnerId(100L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));

        // State machine should reject any transition from CANCELLED (terminal state)
        assertThrows(BusinessException.class, () -> 
            orderService.updateOrderStatus(1L, OrderStatus.OUT_FOR_DELIVERY, 100L, "RESTAURANT_OWNER")
        );
    }
}
