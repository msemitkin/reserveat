package com.reserveat.domain.dto

import com.reserveat.domain.Table

data class TableOutputDto(
    val id: Long,
    val locationId: Long,
    val numberOfSeats: Int
)

fun Table.toTableOutputDto(): TableOutputDto {
    return TableOutputDto(
        id = this.id,
        locationId = this.locationId,
        numberOfSeats = this.numberOfSeats
    )
}