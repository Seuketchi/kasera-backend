package com.boardinghouse.backend.tenants.web

import java.math.BigDecimal
import java.time.LocalDate

data class TenancyResponse (
    val id: Long,
    val tenantId: Long,
    val roomId: Long,
    val monthlyRent: BigDecimal,
    val deposit: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate?,
)