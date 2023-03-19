package com.reserveat.repository.api

import com.reserveat.domain.model.Location
import com.reserveat.domain.model.Photo
import com.reserveat.domain.model.Table
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

interface LocationApi {
    @GetMapping("/api/v1/locations/{locationId}")
    fun getLocation(@PathVariable locationId: String): Location

    @PutMapping("/api/v1/locations/{locationId}")
    fun updateLocation(@PathVariable locationId: String, @RequestBody location: Location): Location

    @DeleteMapping("/api/v1/locations/{locationId}")
    fun deleteLocation(@PathVariable locationId: String)

    @PostMapping("/api/v1/restaurants/{restaurantId}/locations")
    fun createLocation(@PathVariable restaurantId: String, @RequestBody location: Location): Location

    @PostMapping("/api/v1/locations/{locationId}/tables")
    fun createTable(@PathVariable locationId: String, @RequestBody table: Table): Location

    @GetMapping("/api/v1/locations/{locationId}/photos")
    fun getLocationPhotos(@PathVariable locationId: String): List<Photo>

    @PostMapping("/api/v1/locations/{locationId}/photos")
    fun uploadLocationPhoto(@PathVariable locationId: String, @RequestBody photo: Photo): Location

    @GetMapping("/api/v1/locations/nearby")
    fun getNearbyLocations(@RequestParam latitude: Double, @RequestParam longitude: Double): List<Location>
}
