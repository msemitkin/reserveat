package com.reserveat.domain;

public record LocationCoordinates(
    int id,
    int restaurantId,
    double latitude,
    double longitude
) {
}
