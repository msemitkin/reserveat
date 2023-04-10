package com.reserveat.domain

import java.time.LocalDateTime

data class Reservation(
    var id: Int,
    var locationId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val numberOfPeople: Int,
    val table: Table
)
