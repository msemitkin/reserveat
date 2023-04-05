package com.reserveat.service

import com.reserveat.domain.Location
import com.reserveat.domain.LocationCoordinates
import com.reserveat.domain.RestaurantLocation
import com.reserveat.domain.exception.LocationNotFoundException
import com.reserveat.repository.LocationRepository
import org.springframework.stereotype.Service


@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val distanceCalculator: DistanceCalculator
) {

    fun createLocation(restaurantId: Int, location: Location): Location {
        return locationRepository.save(restaurantId, location)
    }

    fun getLocation(locationId: Int): Location {
        return locationRepository.findById(locationId).orElseThrow {
            LocationNotFoundException()
        }
    }

    fun updateLocation(location: Location): Location {
        locationRepository.findById(location.id)
            .orElseThrow { LocationNotFoundException() }
        return locationRepository.update(location)
    }

    fun deleteLocation(locationId: Int) {
        locationRepository.deleteById(locationId)
    }

    fun getNearbyLocations(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<RestaurantLocation> {
        val coordinates: List<LocationCoordinates> = locationRepository.locationsCoordinates
        val filteredLocations = coordinates.filter { coords ->
            distanceCalculator.calculate(
                coords.latitude,
                coords.longitude,
                latitude,
                longitude
            ) <= radiusMeters
        }
        val locationIdRestaurantId = filteredLocations.associate { it.id to it.restaurantId }
        return filteredLocations
            .asSequence()
            .map(LocationCoordinates::id)
            .map(locationRepository::findById)
            .filter { it.isPresent }
            .map { it.get() }
            .map { location ->
                RestaurantLocation(
                    location.id,
                    locationIdRestaurantId[location.id]!!,
                    location.address,
                    location.latitude,
                    location.longitude,
                    location.phone,
                    location.workingHours
                )
            }
            .toList()
    }
}


