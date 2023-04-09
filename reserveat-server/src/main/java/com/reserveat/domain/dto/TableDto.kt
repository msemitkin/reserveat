package com.reserveat.domain.dto

import com.reserveat.domain.Table

data class TableDto(
    val id: Long,
    val locationId: Long,
    val numberOfSeats: Int
)

fun Table.toTableDto(): TableDto {
    return TableDto(
        id = this.id,
        locationId = this.locationId,
        numberOfSeats = this.numberOfSeats
    )
}

