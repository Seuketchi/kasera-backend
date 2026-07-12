package com.boardinghouse.backend.tenants.persistence

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
class Tenancy (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val tenantId: Long = 0,
    val roomId: Long = 0,
    var monthlyRent: BigDecimal = BigDecimal.ZERO,
    var deposit: BigDecimal = BigDecimal.ZERO,
    val startDate: LocalDate = LocalDate.now(),
    var endDate: LocalDate? = null

)