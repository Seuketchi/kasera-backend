package com.boardinghouse.backend.rooms.web

data class RoomResponse(
    val id: Long,
    val propertyId: Long,
    val label: String,
    val monthlyRent: Long,
    val description: String,
    val active: Boolean,
)