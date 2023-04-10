package com.reserveat.web.mapper

import com.reserveat.domain.Reservation
import com.reserveat.web.model.ReservationInputDto
import com.reserveat.web.model.ReservationOutputDto

fun ReservationInputDto.toReservation(tableId: Int): Reservation {
    val startTime = this.startTime
    val endTime = startTime.plusMinutes(this.duration.toLong())
    return Reservation(
        startTime = startTime,
        endTime = endTime,
        tableId = tableId
    )
}

fun Reservation.toOutputDto(locationId: Int): ReservationOutputDto {
    return ReservationOutputDto()
        .id(this.id)
        .locationId(locationId)
        .startTime(this.startTime)
        .endTime(this.endTime)
        .tableId(this.tableId)
}
