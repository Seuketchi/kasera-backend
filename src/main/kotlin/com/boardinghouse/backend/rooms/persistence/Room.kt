package com.boardinghouse.backend.rooms.persistence

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Room (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val propertyId: Long = 0,
    var label: String = "",
    var monthlyRent: BigDecimal = BigDecimal.ZERO,
    var description: String = "",
    var active: Boolean = false,
)