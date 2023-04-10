package com.reserveat.web.mapper

import com.reserveat.domain.Reservation
import com.reserveat.domain.Table
import org.springframework.stereotype.Component

@Component
class ReservationMapper {
    fun toReservation(inputDto: ReservationInputDto, tableId: Int): Reservation {
        val startTime = inputDto.startTime
        val endTime = startTime.plusMinutes(inputDto.duration.toLong())
        return Reservation(
            id = 0,
            locationId = 0,
            startTime = startTime,
            endTime = endTime,
            numberOfPeople = inputDto.numberOfPeople,
            table = Table(tableId, locationId = 0, numberOfSeats = 0)
        )
    }

    fun toOutputDto(reservation: Reservation): ReservationOutputDto {
        return ReservationOutputDto(
            id = reservation.id,
            locationId = reservation.locationId,
            startTime = reservation.startTime,
            endTime = reservation.endTime,
            numberOfPeople = reservation.numberOfPeople,
            tableId = reservation.table.id
        )
    }
}




