package com.boardinghouse.backend.tenants.web

import com.boardinghouse.backend.tenants.persistence.Tenant

fun Tenant.toResponse() = TenantResponse(
    id = id,
    name = name,
    phone = phone,
    email = email,
)

fun CreateTenantRequest.toEntity() = Tenant(
    name = name,
    phone = phone,
    email = email,
)