package com.boardinghouse.backend.rooms.web

data class CreateRoomRequest(
    val label: String,
    val monthlyRent: Long,
    val description: String,
)