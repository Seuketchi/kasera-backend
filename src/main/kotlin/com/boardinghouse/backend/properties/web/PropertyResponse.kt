package com.boardinghouse.backend.properties.web

data class PropertyResponse (
    val id: Long,
    val name: String,
    val location: String,
    val active: Boolean,
)