package com.reserveat.service;

import com.reserveat.domain.Restaurant;
import com.reserveat.domain.exception.RestaurantNotFoundException;
import com.reserveat.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.createRestaurant(restaurant);
    }

    public Restaurant getRestaurant(int id) {
        Optional<Restaurant> restaurant = restaurantRepository.getById(id);
        return restaurant.orElseThrow(RestaurantNotFoundException::new);
    }

    public Restaurant updateRestaurant(Restaurant restaurant) {
        return restaurantRepository.update(restaurant);
    }

    public void deleteRestaurant(int id) {
        restaurantRepository.delete(id);
    }

}
