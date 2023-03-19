package com.reserveat.repository.services

import com.reserveat.domain.model.Location
import com.reserveat.domain.model.Photo
import com.reserveat.domain.model.Table
import com.reserveat.repository.LocationRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.webjars.NotFoundException
import java.util.*


@Service
class LocationService(val locationRepository: LocationRepository) {

    fun getLocation(locationId: String): Location {
        return locationRepository.findById(locationId).orElseThrow { NotFoundException("ERROR") }
    }

    fun updateLocation(locationId: String, location: Location): Location {
        val existingLocation = getLocation(locationId)
        val updatedLocation = existingLocation.copy(
            name = location.name,
            address = location.address,
            latitude = location.latitude,
            longitude = location.longitude,
            tables = location.tables,
            photos = location.photos
        )
        return locationRepository.save(updatedLocation)
    }

    fun deleteLocation(locationId: String) {
        locationRepository.deleteById(locationId)
    }

    fun createLocation(restaurantId: String, location: Location): Location {
        val newLocation = location.copy(id = UUID.randomUUID().toString())
        return locationRepository.save(newLocation)
    }

    fun createTable(locationId: String, table: Table): Location {
        val location = getLocation(locationId)
        val updatedTables = location.tables.toMutableList().apply { add(table) }
        val updatedLocation = location.copy(tables = updatedTables)
        return locationRepository.save(updatedLocation)
    }

    fun getPhotos(locationId: String): List<Photo> {
        val location = getLocation(locationId)
        return location.photos
    }

    fun addPhoto(locationId: String, photo: Photo): Location {
        val location = getLocation(locationId)
        val updatedPhotos = location.photos.toMutableList().apply { add(photo) }
        val updatedLocation = location.copy(photos = updatedPhotos)
        return locationRepository.save(updatedLocation)
    }

    fun getNearbyLocations(latitude: Double, longitude: Double): List<Location> {
        // TODO: implement nearby location search
        return emptyList()
    }
}


