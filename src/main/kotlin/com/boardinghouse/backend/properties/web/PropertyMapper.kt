package com.boardinghouse.backend.properties.web

import com.boardinghouse.backend.properties.persistence.Property


fun Property.toResponse() = PropertyResponse(
    id = id,
    name = name,
    location = location,
    active = active,
)

fun CreatePropertyRequest.toEntity() = Property(
    name = name,
    location = location,
)