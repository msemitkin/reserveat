package com.reserveat.domain.dto

import com.reserveat.domain.Table
import jakarta.validation.constraints.Min

data class TableInputDto(
    @field:Min(value = 1)
    val numberOfSeats: Int?
)

fun TableInputDto.toTable(): Table {
    return Table(
        id = 0,
        locationId = 0,
        numberOfSeats = this.numberOfSeats!!
    )
}

