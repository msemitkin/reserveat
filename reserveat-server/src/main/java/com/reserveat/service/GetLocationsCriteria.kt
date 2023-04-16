package com.reserveat.service

import java.time.LocalDateTime

data class GetLocationsCriteria(
    val numberOfVisitors: Int?,
    val name: String?,
    val dateTimeFrom: LocalDateTime?,
    val dateTimeTo: LocalDateTime?
) {
    init {
        if ((dateTimeFrom != null) xor (dateTimeTo != null)) {
            throw IllegalArgumentException()
        }
    }
}
