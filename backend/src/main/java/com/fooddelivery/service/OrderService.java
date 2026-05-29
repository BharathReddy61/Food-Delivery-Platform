package com.fooddelivery.service;

import com.fooddelivery.dto.CreateOrderRequestDto;
import com.fooddelivery.dto.OrderItemResponseDto;
import com.fooddelivery.dto.OrderResponseDto;
import com.fooddelivery.entity.Cart;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderItem;
import com.fooddelivery.entity.User;
import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.CartItemRepository;
import com.fooddelivery.repository.CartRepository;
import com.fooddelivery.repository.OrderItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public OrderResponseDto createOrder(Long userId, CreateOrderRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cannot checkout empty cart."));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot checkout empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setRestaurantId(cart.getRestaurantId());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setStatus(OrderStatus.PENDING);

        double calculatedTotal = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItemId(cartItem.getMenuItem().getId());
            orderItem.setMenuItemName(cartItem.getMenuItem().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setItemPrice(cartItem.getItemPrice());
            orderItem.setItemTotal(cartItem.getItemTotal());
            
            calculatedTotal += orderItem.getItemTotal();
            order.getItems().add(orderItem);
        }

        if (calculatedTotal <= 0) {
            throw new BusinessException("Order total must be greater than zero.");
        }

        order.setTotalAmount(calculatedTotal);

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cart.setRestaurantId(null);
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        return mapToResponseDto(savedOrder);
    }

    public List<OrderResponseDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponseDto getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("You do not have permission to view this order.");
        }

        return mapToResponseDto(order);
    }

    public List<OrderResponseDto> getOwnerOrders(Long ownerId) {
        List<com.fooddelivery.entity.Restaurant> ownedRestaurants = restaurantRepository.findByOwnerId(ownerId);
        List<Long> restaurantIds = ownedRestaurants.stream()
                .map(com.fooddelivery.entity.Restaurant::getId)
                .collect(Collectors.toList());

        if (restaurantIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Order> orders = orderRepository.findByRestaurantIdIn(restaurantIds);
        return orders.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponseDto getOwnerOrderById(Long ownerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        com.fooddelivery.entity.Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (!ownerId.equals(restaurant.getOwnerId())) {
            throw new BusinessException("You do not have permission to view this order.");
        }

        return mapToResponseDto(order);
    }

    public com.fooddelivery.dto.OwnerDashboardDto getOwnerDashboardData(Long ownerId) {
        List<com.fooddelivery.entity.Restaurant> ownedRestaurants = restaurantRepository.findByOwnerId(ownerId);
        List<Long> restaurantIds = ownedRestaurants.stream()
                .map(com.fooddelivery.entity.Restaurant::getId)
                .collect(Collectors.toList());

        com.fooddelivery.dto.OwnerDashboardDto dashboard = new com.fooddelivery.dto.OwnerDashboardDto();
        dashboard.setRestaurants(ownedRestaurants);

        if (restaurantIds.isEmpty()) {
            dashboard.setRecentOrders(java.util.Collections.emptyList());
            return dashboard;
        }

        List<Order> allOrders = orderRepository.findByRestaurantIdIn(restaurantIds);
        
        long active = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .count();
        long completed = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();
        long cancelled = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED)
                .count();
        double revenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        dashboard.setActiveOrderCount(active);
        dashboard.setCompletedOrderCount(completed);
        dashboard.setCancelledOrderCount(cancelled);
        dashboard.setTotalRevenue(revenue);

        // Recent 5 orders
        List<OrderResponseDto> recent = allOrders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
        
        dashboard.setRecentOrders(recent);

        return dashboard;
    }

    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus, Long userId, String userRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        com.fooddelivery.entity.Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (!userRole.equals("ADMIN") && !userId.equals(restaurant.getOwnerId())) {
            throw new BusinessException("You do not have permission to manage this order");
        }

        validateStatusTransition(order.getStatus(), newStatus);
        
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        
        return mapToResponseDto(savedOrder);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!currentStatus.getAllowedTransitions().contains(newStatus.name())) {
            throw new BusinessException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    public OrderResponseDto mapToResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setRestaurantId(order.getRestaurantId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponseDto> items = order.getItems().stream().map(item -> {
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setMenuItemId(item.getMenuItemId());
            itemDto.setMenuItemName(item.getMenuItemName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setItemPrice(item.getItemPrice());
            itemDto.setItemTotal(item.getItemTotal());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(items);
        dto.setAllowedTransitions(order.getStatus().getAllowedTransitions());
        return dto;
    }
}
