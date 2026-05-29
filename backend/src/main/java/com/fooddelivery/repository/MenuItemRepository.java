package com.fooddelivery.repository;

import com.fooddelivery.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantId(Long restaurantId);

    List<MenuItem> findByCategory(String category);

    List<MenuItem> findByAvailable(Boolean available);

    List<MenuItem> findByRestaurantIdAndAvailable(
            Long restaurantId,
            Boolean available
    );
}