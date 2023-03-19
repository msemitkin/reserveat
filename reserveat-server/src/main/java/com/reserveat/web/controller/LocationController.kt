package com.reserveat.web.controller

import com.reserveat.domain.model.Location
import com.reserveat.domain.model.Photo
import com.reserveat.domain.model.Table
import com.reserveat.repository.services.LocationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class LocationController(val locationService: LocationService) {

    @GetMapping("/locations/{locationId}")
    fun getLocation(@PathVariable locationId: String): Location {
        return locationService.getLocation(locationId)
    }

    @PutMapping("/locations/{locationId}")
    fun updateLocation(@PathVariable locationId: String, @RequestBody location: Location): Location {
        return locationService.updateLocation(locationId, location)
    }

    @DeleteMapping("/locations/{locationId}")
    fun deleteLocation(@PathVariable locationId: String) {
        locationService.deleteLocation(locationId)
    }

    @PostMapping("/restaurants/{restaurantId}/locations")
    fun createLocation(@PathVariable restaurantId: String, @RequestBody location: Location): Location {
        return locationService.createLocation(restaurantId, location)
    }

    @PostMapping("/locations/{locationId}/tables")
    fun createTable(@PathVariable locationId: String, @RequestBody table: Table): Location {
        return locationService.createTable(locationId, table)
    }

    @GetMapping("/locations/{locationId}/photos")
    fun getPhotos(@PathVariable locationId: String): List<Photo> {
        return locationService.getPhotos(locationId)
    }

    @PostMapping("/locations/{locationId}/photos")
    fun addPhoto(@PathVariable locationId: String, @RequestBody photo: Photo): Location {
        return locationService.addPhoto(locationId, photo)
    }

    @GetMapping("/locations/nearby")
    fun getNearbyLocations(@RequestParam latitude: Double, @RequestParam longitude: Double): List<Location> {
        return locationService.getNearbyLocations(latitude, longitude)
    }

}
