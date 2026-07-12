package com.boardinghouse.backend.tenants.web

data class UpdateTenantRequest(
    val name: String,
    val phone: String?,
    val email: String?,
)
