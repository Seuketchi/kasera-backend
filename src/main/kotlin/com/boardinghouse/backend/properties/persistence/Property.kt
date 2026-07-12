package com.boardinghouse.backend.properties.persistence

import jakarta.persistence.*


@Entity
class Property (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String = "",
    var location: String = "",
    var active: Boolean = true,

)