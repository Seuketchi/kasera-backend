package com.boardinghouse.backend.rooms.web

data class UpdateRoomRequest(
    val label: String,
    val monthlyRent: Long,
    val description: String,
)