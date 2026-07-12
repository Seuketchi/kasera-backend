package com.boardinghouse.backend.rooms.persistence

import jakarta.persistence.*

@Entity
class Room (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val propertyId: Long = 0,
    var label: String = "",
    var monthlyRent: Long = 0,
    var description: String = "",
    var active: Boolean = false,
)