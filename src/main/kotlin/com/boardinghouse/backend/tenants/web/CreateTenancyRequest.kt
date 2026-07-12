package com.boardinghouse.backend.tenants.web

import java.math.BigDecimal
import java.time.LocalDate

data class CreateTenancyRequest(
    val tenantId: Long,
    val startDate: LocalDate,
    val deposit: BigDecimal,
)
