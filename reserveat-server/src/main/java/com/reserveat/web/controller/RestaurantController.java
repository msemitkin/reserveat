package com.reserveat.web.controller;

import com.reserveat.domain.Restaurant;
import com.reserveat.service.RestaurantService;
import com.reserveat.web.api.RestaurantApi;
import com.reserveat.web.mapper.RestaurantMapper;
import com.reserveat.web.model.RestaurantInputDto;
import com.reserveat.web.model.RestaurantOutputDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController implements RestaurantApi {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public ResponseEntity<RestaurantOutputDto> createRestaurant(RestaurantInputDto restaurantInputDto) {
        Restaurant restaurant = new Restaurant(restaurantInputDto.getName(), restaurantInputDto.getDescription());

        Restaurant created = restaurantService.createRestaurant(restaurant);

        RestaurantOutputDto responseDto = RestaurantMapper.fromRestaurant(created);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<RestaurantOutputDto> getRestaurant(Integer restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);

        RestaurantOutputDto responseDto = RestaurantMapper.fromRestaurant(restaurant);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<RestaurantOutputDto> updateRestaurant(
        Integer restaurantId,
        RestaurantInputDto restaurantInputDto
    ) {
        Restaurant restaurant = new Restaurant(
            restaurantId,
            restaurantInputDto.getName(),
            restaurantInputDto.getDescription()
        );

        Restaurant updated = restaurantService.updateRestaurant(restaurant);

        RestaurantOutputDto responseDto = RestaurantMapper.fromRestaurant(updated);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<Void> deleteRestaurant(Integer restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok().build();
    }
}
