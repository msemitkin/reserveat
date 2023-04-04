package com.reserveat.service

import com.reserveat.domain.Location
import com.reserveat.domain.RestaurantLocation
import com.reserveat.domain.exception.LocationNotFoundException
import com.reserveat.repository.LocationRepository
import org.springframework.stereotype.Service


@Service
class LocationService(private val locationRepository: LocationRepository) {

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
        TODO()
    }
}


