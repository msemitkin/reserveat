package com.reserveat.web.controller;

import com.reserveat.domain.Location;
import com.reserveat.service.GetLocationsCriteria;
import com.reserveat.service.LocationService;
import com.reserveat.service.Slot;
import com.reserveat.web.api.LocationApi;
import com.reserveat.web.mapper.LocationMapper;
import com.reserveat.web.model.GetLocationsByCriteriaCriteriaParameterDto;
import com.reserveat.web.model.LocationInputDto;
import com.reserveat.web.model.LocationOutputDto;
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
        Location location = LocationMapper.toLocation(locationInputDto, restaurantId);
        Location savedLocation = locationService.createLocation(location);
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
        Location existingLocation = locationService.getLocation(locationId);
        Location location = LocationMapper.toLocation(locationId, locationInputDto, existingLocation.restaurantId());
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
        List<Location> nearbyLocations = locationService.getNearbyLocations(latitude, longitude, radius);
        var locationWithRestaurantOutputDtos = LocationMapper.toDtos(nearbyLocations);
        return ResponseEntity.ok(new LocationsWithRestaurantOutputDto().locations(locationWithRestaurantOutputDtos));
    }

    @Override
    public ResponseEntity<LocationsWithRestaurantOutputDto> getLocationsByCriteria(
        GetLocationsByCriteriaCriteriaParameterDto criteria
    ) {
        validateCriteria(criteria);
        List<Location> locations = locationService.getLocationsByCriteria(new GetLocationsCriteria(
            criteria.getVisitors(),
            criteria.getName(),
            new Slot(criteria.getDateTimeFrom(), criteria.getDateTimeTo())
        ));
        return ResponseEntity.ok(new LocationsWithRestaurantOutputDto().locations(LocationMapper.toDtos(locations)));
    }

    private void validateCriteria(GetLocationsByCriteriaCriteriaParameterDto criteria) {
        if (criteria.getDateTimeFrom().isAfter(criteria.getDateTimeTo())) {
            throw new IllegalArgumentException("Time range is invalid");
        }
    }
}
