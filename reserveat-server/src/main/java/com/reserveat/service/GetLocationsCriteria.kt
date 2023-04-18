package com.reserveat.service

data class GetLocationsCriteria(
    val numberOfVisitors: Int?,
    val name: String?,
    val slot: Slot
)
