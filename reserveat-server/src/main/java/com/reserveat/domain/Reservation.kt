package com.reserveat.domain

import java.time.LocalDateTime

data class Reservation(
    var id: Int? = null,
    var tableId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)
