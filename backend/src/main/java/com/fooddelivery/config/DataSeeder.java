package com.fooddelivery.config;

import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;

import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;

import com.github.javafaker.Faker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    private final Faker faker = new Faker();

    private final Random random = new Random();

    /*
     * Food Images
     */

    private final List<String> foodImages = List.of(

            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38",

            "https://images.unsplash.com/photo-1550547660-d9450f859349",

            "https://images.unsplash.com/photo-1512058564366-18510be2db19",

            "https://images.unsplash.com/photo-1544025162-d76694265947",

            "https://images.unsplash.com/photo-1504674900247-0877df9cc836",

            "https://images.unsplash.com/photo-1563379091339-03246963d96c",

            "https://images.unsplash.com/photo-1527477396000-e27163b481c2",

            "https://images.unsplash.com/photo-1513104890138-7c749659a591");

    /*
     * Categories
     */

    private final List<String> categories = List.of(

            "Biryani",

            "Pizza",

            "Burger",

            "Desserts",

            "Breakfast",

            "South Indian",

            "North Indian",

            "Beverages",

            "Chinese",

            "Grill");

    @Bean
    CommandLineRunner seedMenuItems(

            RestaurantRepository restaurantRepository,

            MenuItemRepository menuItemRepository) {

        return args -> {

            /*
             * Avoid duplicate menu generation
             */

            if (menuItemRepository.count() > 500) {

                System.out.println(
                        "Menu items already seeded.");

                return;
            }

            List<Restaurant> restaurants = restaurantRepository.findAll();

            for (Restaurant restaurant : restaurants) {

                /*
                 * Generate 15-20 items
                 */

                int itemCount = 15 + random.nextInt(6);

                for (int i = 0; i < itemCount; i++) {

                    try {

                        MenuItem item = new MenuItem();

                        item.setRestaurant(restaurant);

                        /*
                         * Safer Faker API
                         */

                        item.setName(
                                faker.commerce().productName());

                        item.setDescription(
                                faker.lorem().sentence(10));

                        item.setPrice(
                                99.0 + random.nextInt(500));

                        item.setAvailable(true);

                        item.setCategory(
                                categories.get(
                                        random.nextInt(
                                                categories.size())));

                        item.setImageUrl(
                                foodImages.get(
                                        random.nextInt(
                                                foodImages.size())));

                        menuItemRepository.save(item);

                    } catch (Exception e) {

                        System.out.println(
                                "Seeder skipped one menu item: "
                                        + e.getMessage());
                    }
                }
            }

            System.out.println(
                    "Realistic menu data seeded successfully.");
        };
    }
}