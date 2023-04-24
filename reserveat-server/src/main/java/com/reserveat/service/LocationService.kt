package com.reserveat.service

import com.reserveat.domain.Location
import com.reserveat.domain.LocationCoordinates
import com.reserveat.domain.Restaurant
import com.reserveat.domain.Table
import com.reserveat.domain.exception.LocationNotFoundException
import com.reserveat.repository.LocationRepository
import com.reserveat.repository.RestaurantRepository
import org.springframework.stereotype.Service


@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val distanceCalculator: DistanceCalculator,
    private val restaurantRepository: RestaurantRepository,
    private val reservationService: ReservationService
) {

    fun createLocation(location: Location): Location {
        return locationRepository.save(location)
    }

    fun getLocation(locationId: Int): Location {
        return locationRepository.findById(locationId).orElseThrow {
            LocationNotFoundException()
        }
    }

    fun getLocationByReservationId(id: Int): Location {
        return locationRepository.findByReservationId(id).get()
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
    ): List<Location> {
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
                Location(
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

    fun getLocationsByCriteria(criteria: GetLocationsCriteria): List<Location> {
        val restaurantsByName = getRestaurantsByName(criteria)
        val locationsByName = restaurantsByName.flatMap { locationRepository.getByRestaurantId(it.id) }
        val locationsWithSlots: Map<Location, Map<Table, List<Slot>>> = locationsByName.asSequence()
            .associateWith { reservationService.getFreeSlots(it.id, criteria.slot.from.toLocalDate()) }
            .filter { it.value.isNotEmpty() }
            .toMap()
        return locationsWithSlots
            .filter { (_, tableSlots) ->
                tableSlots.any { (table, slots) ->
                    slots.any { slot ->
                        slot.contains(criteria.slot)
                                && (criteria.numberOfVisitors == null || table.numberOfSeats >= criteria.numberOfVisitors)
                    }
                }
            }.keys.toList()
    }

    private fun getRestaurantsByName(criteria: GetLocationsCriteria): List<Restaurant> {
        val restaurants = restaurantRepository.all
        return if (criteria.name != null) {
            restaurants.filter { it.name.lowercase().contains(criteria.name.lowercase()) }
        } else {
            restaurants
        }
    }
}
