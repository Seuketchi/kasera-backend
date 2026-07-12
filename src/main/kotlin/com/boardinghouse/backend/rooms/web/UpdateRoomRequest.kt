package com.boardinghouse.backend.rooms.web

import java.math.BigDecimal

data class UpdateRoomRequest(
    val label: String,
    val monthlyRent: BigDecimal,
    val description: String,
)