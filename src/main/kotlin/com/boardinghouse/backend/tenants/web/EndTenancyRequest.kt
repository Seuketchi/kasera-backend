package com.boardinghouse.backend.tenants.web

import java.time.LocalDate

data class EndTenancyRequest(
    val endDate: LocalDate,
)
