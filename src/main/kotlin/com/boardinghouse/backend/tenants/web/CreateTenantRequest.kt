package com.boardinghouse.backend.tenants.web

data class CreateTenantRequest (
    val name: String,
    val phone: String?,
    val email: String?,
)