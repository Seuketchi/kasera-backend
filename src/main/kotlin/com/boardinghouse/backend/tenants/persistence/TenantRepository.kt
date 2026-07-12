package com.boardinghouse.backend.tenants.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TenantRepository : JpaRepository<Tenant, Long> {
}