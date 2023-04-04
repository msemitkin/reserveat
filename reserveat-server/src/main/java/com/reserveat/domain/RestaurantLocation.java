package com.reserveat.domain;

import com.reserveat.web.DayWorkingHours;

import java.time.DayOfWeek;
import java.util.Map;

public record RestaurantLocation(
    Integer id,
    int restaurantId,
    String address,
    Double latitude,
    Double longitude,
    String phone,
    Map<DayOfWeek, DayWorkingHours> workingHours
) {
}
