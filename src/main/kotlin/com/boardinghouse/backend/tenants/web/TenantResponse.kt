package com.boardinghouse.backend.tenants.web

data class TenantResponse (
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
)