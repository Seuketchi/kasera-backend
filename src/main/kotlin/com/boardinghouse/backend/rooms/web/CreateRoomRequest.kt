package com.boardinghouse.backend.rooms.web

import java.math.BigDecimal

data class CreateRoomRequest(
    val label: String,
    val monthlyRent: BigDecimal,
    val description: String,
)