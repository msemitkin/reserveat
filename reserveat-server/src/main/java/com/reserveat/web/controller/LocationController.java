package com.reserveat.web.controller;

import com.reserveat.domain.Location;
import com.reserveat.domain.RestaurantLocation;
import com.reserveat.service.LocationService;
import com.reserveat.web.api.LocationApi;
import com.reserveat.web.mapper.LocationMapper;
import com.reserveat.web.model.LocationInputDto;
import com.reserveat.web.model.LocationOutputDto;
import com.reserveat.web.model.LocationWithRestaurantOutputDto;
import com.reserveat.web.model.LocationsWithRestaurantOutputDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController implements LocationApi {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public ResponseEntity<LocationOutputDto> createLocation(Integer restaurantId, LocationInputDto locationInputDto) {
        Location location = LocationMapper.toLocation(locationInputDto);
        Location savedLocation = locationService.createLocation(restaurantId, location);
        LocationOutputDto locationOutputDto = LocationMapper.fromLocation(savedLocation);
        return ResponseEntity.ok(locationOutputDto);
    }

    @Override
    public ResponseEntity<LocationOutputDto> getLocationById(Integer locationId) {
        Location location = locationService.getLocation(locationId);
        LocationOutputDto locationOutputDto = LocationMapper.fromLocation(location);
        return ResponseEntity.ok(locationOutputDto);
    }

    @Override
    public ResponseEntity<LocationOutputDto> updateLocation(Integer locationId, LocationInputDto locationInputDto) {
        Location location = LocationMapper.toLocation(locationId, locationInputDto);
        Location updatedLocation = locationService.updateLocation(location);
        LocationOutputDto locationOutputDto = LocationMapper.fromLocation(updatedLocation);
        return ResponseEntity.ok(locationOutputDto);
    }

    @Override
    public ResponseEntity<Void> deleteLocation(Integer locationId) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<LocationsWithRestaurantOutputDto> getNearbyLocations(Float latitude, Float longitude, Integer radius) {
        List<RestaurantLocation> nearbyLocations = locationService.getNearbyLocations(latitude, longitude, radius);
        var locationWithRestaurantOutputDtos = nearbyLocations.stream()
            .map(location -> new LocationWithRestaurantOutputDto()
                .id(location.id())
                .restaurantId(location.restaurantId())
                .address(location.address())
                .latitude(location.latitude())
                .longitude(location.longitude()))
            .toList();
        return ResponseEntity.ok(new LocationsWithRestaurantOutputDto().locations(locationWithRestaurantOutputDtos));
    }
}
