package com.fooddelivery.service;

import com.fooddelivery.dto.MenuItemRequestDto;
import com.fooddelivery.dto.MenuItemResponseDto;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public MenuItemResponseDto createMenuItem(
            Long restaurantId,
            MenuItemRequestDto requestDto,
            Long userId,
            String userRole) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + restaurantId));

        if (!userRole.equals("ADMIN")
                && !userId.equals(restaurant.getOwnerId())) {

            throw new BusinessException(
                    "You do not have permission to manage this restaurant's menu");
        }

        MenuItem menuItem = new MenuItem();

        menuItem.setName(requestDto.getName());
        menuItem.setDescription(requestDto.getDescription());
        menuItem.setPrice(requestDto.getPrice());
        menuItem.setCategory(requestDto.getCategory());
        menuItem.setImageUrl(requestDto.getImageUrl());

        menuItem.setRestaurant(restaurant);

        menuItem.setAvailable(
                requestDto.getAvailable() != null
                        ? requestDto.getAvailable()
                        : true);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        return mapToResponseDto(savedMenuItem);
    }

    public MenuItemResponseDto updateMenuItem(
            Long menuItemId,
            MenuItemRequestDto requestDto,
            Long userId,
            String userRole) {

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found"));

        Restaurant restaurant = menuItem.getRestaurant();

        if (!userRole.equals("ADMIN")
                && !userId.equals(restaurant.getOwnerId())) {

            throw new BusinessException(
                    "You do not have permission to manage this menu item");
        }

        menuItem.setName(requestDto.getName());
        menuItem.setDescription(requestDto.getDescription());
        menuItem.setPrice(requestDto.getPrice());
        menuItem.setCategory(requestDto.getCategory());
        menuItem.setImageUrl(requestDto.getImageUrl());

        if (requestDto.getAvailable() != null) {
            menuItem.setAvailable(requestDto.getAvailable());
        }

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        return mapToResponseDto(savedMenuItem);
    }

    public void deleteMenuItem(
            Long menuItemId,
            Long userId,
            String userRole) {

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item not found"));

        Restaurant restaurant = menuItem.getRestaurant();

        if (!userRole.equals("ADMIN")
                && !userId.equals(restaurant.getOwnerId())) {

            throw new BusinessException(
                    "You do not have permission to delete this menu item");
        }

        menuItemRepository.delete(menuItem);
    }

    public List<MenuItemResponseDto> getMenuByRestaurant(
            Long restaurantId) {

        validateRestaurantExists(restaurantId);

        List<MenuItem> items = menuItemRepository.findByRestaurantId(restaurantId);

        return items.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponseDto> getAvailableMenuItems(
            Long restaurantId) {

        validateRestaurantExists(restaurantId);

        List<MenuItem> items = menuItemRepository.findByRestaurantIdAndAvailable(
                restaurantId,
                true);

        return items.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponseDto> getMenuItemsByCategory(
            String category) {

        List<MenuItem> items = menuItemRepository.findByCategory(category);

        return items.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private void validateRestaurantExists(Long restaurantId) {

        if (!restaurantRepository.existsById(restaurantId)) {

            throw new ResourceNotFoundException(
                    "Restaurant not found with id: " + restaurantId);
        }
    }

    private MenuItemResponseDto mapToResponseDto(
            MenuItem menuItem) {

        MenuItemResponseDto dto = new MenuItemResponseDto();

        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.getAvailable());
        dto.setCategory(menuItem.getCategory());
        dto.setImageUrl(menuItem.getImageUrl());

        if (menuItem.getRestaurant() != null) {

            dto.setRestaurantId(
                    menuItem.getRestaurant().getId());

            dto.setRestaurantName(
                    menuItem.getRestaurant().getName());
        }

        return dto;
    }
}