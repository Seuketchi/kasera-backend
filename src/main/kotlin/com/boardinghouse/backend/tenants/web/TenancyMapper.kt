package com.boardinghouse.backend.tenants.web

import com.boardinghouse.backend.tenants.persistence.Tenancy



fun Tenancy.toResponse() = TenancyResponse(
    id = id,
    tenantId = tenantId,
    roomId = roomId,
    monthlyRent = monthlyRent,
    deposit = deposit,
    startDate = startDate,
    endDate = endDate,

)

