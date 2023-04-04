package com.reserveat.web.mapper;

import com.reserveat.domain.Restaurant;
import com.reserveat.web.model.RestaurantOutputDto;
import org.springframework.lang.NonNull;

public class RestaurantMapper {
    private RestaurantMapper() {
    }

    @NonNull
    public static RestaurantOutputDto fromRestaurant(@NonNull Restaurant restaurant) {
        return new RestaurantOutputDto()
            .id(restaurant.getId())
            .name(restaurant.getName())
            .description(restaurant.getDescription());
    }
}
