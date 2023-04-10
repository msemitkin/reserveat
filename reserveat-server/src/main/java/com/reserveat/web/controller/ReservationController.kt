package com.reserveat.web.controller

import com.reserveat.service.LocationService
import com.reserveat.service.ReservationService
import com.reserveat.web.api.ReservationApi
import com.reserveat.web.exception.ResourceNotFoundException
import com.reserveat.web.mapper.toOutputDto
import com.reserveat.web.mapper.toReservation
import com.reserveat.web.model.ReservationInputDto
import com.reserveat.web.model.ReservationOutputDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
open class ReservationController(
    private val reservationService: ReservationService,
    private val locationService: LocationService
) : ReservationApi {

    override fun createReservation(
        tableId: Int,
        reservationInputDto: ReservationInputDto
    ): ResponseEntity<ReservationOutputDto> {
        val reservation = reservationInputDto.toReservation(tableId)

        val savedReservation = reservationService.createReservation(reservation)

        val location = locationService.getLocationByReservationId(savedReservation.id!!)
        val outputDto = savedReservation.toOutputDto(location.id)
        return ResponseEntity.ok(outputDto)
    }

    override fun getReservationById(reservationId: Int): ResponseEntity<ReservationOutputDto> {
        val reservation = reservationService.getReservationById(reservationId)
            ?: throw ResourceNotFoundException("Reservation not found")
        val location = locationService.getLocationByReservationId(reservationId)
        val outputDto = reservation.toOutputDto(location.id)
        return ResponseEntity.ok(outputDto)
    }

    override fun deleteReservation(reservationId: Int): ResponseEntity<Void> {
        reservationService.deleteReservation(reservationId)
        return ResponseEntity.ok().build()
    }
}
