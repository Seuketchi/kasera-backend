package com.boardinghouse.backend.tenants.persistence

import jakarta.persistence.*

@Entity
class Tenant (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String = "",
    var phone: String? = null,
    var email: String? = null,
)