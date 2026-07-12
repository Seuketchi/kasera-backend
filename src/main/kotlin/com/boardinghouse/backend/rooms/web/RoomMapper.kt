package com.boardinghouse.backend.rooms.web

import com.boardinghouse.backend.rooms.persistence.Room

fun Room.toResponse() = RoomResponse(
    id = id,
    propertyId = propertyId,
    label = label,
    monthlyRent = monthlyRent,
    description = description,
    active = active
)

fun CreateRoomRequest.toEntity(propertyId: Long) = Room(
    propertyId = propertyId,
    label = label,
    monthlyRent = monthlyRent,
    description = description,
    active = true
)