package com.fooddelivery.service;

import com.fooddelivery.dto.AddToCartRequestDto;
import com.fooddelivery.dto.CartItemResponseDto;
import com.fooddelivery.dto.CartResponseDto;
import com.fooddelivery.entity.Cart;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.CartItemRepository;
import com.fooddelivery.repository.CartRepository;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            MenuItemRepository menuItemRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }

    public CartResponseDto addToCart(Long userId, AddToCartRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return newCart;
                });

        MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + dto.getMenuItemId()));

        if (menuItem.getAvailable() == null || !menuItem.getAvailable()) {
            throw new BusinessException("Menu item is currently unavailable");
        }

        Long itemRestaurantId = menuItem.getRestaurant().getId();

        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(itemRestaurantId)) {
            throw new BusinessException("Cart can only contain items from a single restaurant. Clear your cart to start a new order.");
        }

        if (cart.getRestaurantId() == null) {
            cart.setRestaurantId(itemRestaurantId);
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            existingItem.setItemTotal(existingItem.getQuantity() * existingItem.getItemPrice());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setMenuItem(menuItem);
            newItem.setQuantity(dto.getQuantity());
            newItem.setItemPrice(menuItem.getPrice());
            newItem.setItemTotal(dto.getQuantity() * menuItem.getPrice());
            cart.getItems().add(newItem);
        }

        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return mapToCartResponseDto(savedCart);
    }

    public CartResponseDto getCart(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            CartResponseDto emptyDto = new CartResponseDto();
            emptyDto.setTotalAmount(0.0);
            emptyDto.setItems(new ArrayList<>());
            return emptyDto;
        }
        return mapToCartResponseDto(cartOpt.get());
    }

    public CartResponseDto removeCartItem(Long userId, Long menuItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found in cart"));

        cart.getItems().remove(itemToRemove);

        if (cart.getItems().isEmpty()) {
            cart.setRestaurantId(null);
        }

        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return mapToCartResponseDto(savedCart);
    }

    public CartResponseDto updateCartItemQuantity(Long userId, Long menuItemId, int newQuantity) {
        if (newQuantity < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getMenuItem().getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        // REPLACEMENT semantics — not additive
        item.setQuantity(newQuantity);
        item.setItemTotal(newQuantity * item.getItemPrice());

        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return mapToCartResponseDto(savedCart);
    }

    private void recalculateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getItemTotal)
                .sum();
        cart.setTotalAmount(total);
    }

    private CartResponseDto mapToCartResponseDto(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(cart.getId());
        dto.setTotalAmount(cart.getTotalAmount());

        List<CartItemResponseDto> itemDtos = cart.getItems().stream().map(item -> {
            CartItemResponseDto itemDto = new CartItemResponseDto();
            itemDto.setMenuItemId(item.getMenuItem().getId());
            itemDto.setMenuItemName(item.getMenuItem().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setItemPrice(item.getItemPrice());
            itemDto.setItemTotal(item.getItemTotal());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}
