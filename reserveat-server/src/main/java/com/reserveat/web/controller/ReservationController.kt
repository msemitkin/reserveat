package com.reserveat.web.controller

import com.reserveat.service.ReservationService
import com.reserveat.web.exception.ReservationConflictException
import com.reserveat.web.exception.ResourceNotFoundException
import com.reserveat.web.mapper.ReservationMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/locations/{locationId}/reservations")
class ReservationController(
    private val reservationService: ReservationService,
    private val reservationMapper: ReservationMapper
) {
    @PostMapping("")
    fun createReservation(
        @PathVariable locationId: Int,
        @RequestBody inputDto: ReservationInputDto
    ): ResponseEntity<ReservationOutputDto> {
            val reservation = reservationMapper.toReservation(inputDto)
            reservation.locationId = locationId

            try {
                val savedReservation = reservationService.createReservation(reservation)
                val outputDto = reservationMapper.toOutputDto(savedReservation)
                return ResponseEntity.ok(outputDto)
            } catch (ex: ReservationConflictException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build()
            } catch (ex: ResourceNotFoundException) {
                return ResponseEntity.notFound().build()
            }
        }

    @GetMapping("/{reservationId}")
    fun getReservationById(
        @PathVariable reservationId: Int,
        @PathVariable locationId: String
    ): ResponseEntity<ReservationOutputDto> {
        val reservation = reservationService.getReservationById(reservationId)

        if (reservation != null) {
            val outputDto = reservationMapper.toOutputDto(reservation)
            return ResponseEntity.ok(outputDto)
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{reservationId}")
    fun updateReservationById(
        @PathVariable reservationId: Int,
        @RequestBody inputDto: ReservationInputDto
    ): ResponseEntity<Unit> {
        val reservation = reservationMapper.toReservation(inputDto)
        reservation.id = reservationId

        try {
            reservationService.updateReservation(reservation)
            return ResponseEntity.noContent().build()
        } catch (ex: ResourceNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{reservationId}")
    fun deleteReservationById(
        @PathVariable reservationId: Int
    ): ResponseEntity<Unit> {
        try {
            reservationService.deleteReservation(reservationId)
            return ResponseEntity.noContent().build()
        } catch (ex: ResourceNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }
}