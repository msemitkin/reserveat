package com.reserveat.web.mapper

import com.reserveat.domain.Table
import com.reserveat.web.model.TableOutputDto

fun Table.toTableOutputDto(): TableOutputDto {
    return TableOutputDto()
        .id(this.id)
        .locationId(this.locationId)
        .numberOfSeats(this.numberOfSeats)
}
