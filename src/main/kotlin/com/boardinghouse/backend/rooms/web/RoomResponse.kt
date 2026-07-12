package com.boardinghouse.backend.rooms.web

import java.math.BigDecimal

data class RoomResponse(
    val id: Long,
    val propertyId: Long,
    val label: String,
    val monthlyRent: BigDecimal,
    val description: String,
    val active: Boolean,
)