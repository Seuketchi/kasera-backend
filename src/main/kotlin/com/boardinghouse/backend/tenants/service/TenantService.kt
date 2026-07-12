package com.boardinghouse.backend.tenants.service


import com.boardinghouse.backend.shared.NotFoundException
import com.boardinghouse.backend.tenants.persistence.*
import com.boardinghouse.backend.tenants.web.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TenantService (
    private val tenantRepository: TenantRepository,
) {
    @Transactional
    fun createTenant(request: CreateTenantRequest): Tenant =
        tenantRepository.save(request.toEntity())

    fun findTenantById(id: Long): Tenant =
        tenantRepository.findById(id).orElseThrow {
            NotFoundException("Tenant with id $id not found")
        }

    fun findAllTenants(): List<Tenant> =
        tenantRepository.findAll()

    @Transactional
    fun updateTenant(id: Long, request: UpdateTenantRequest): Tenant {
        val tenant = findTenantById(id)
        tenant.name = request.name
        tenant.email = request.email
        tenant.phone = request.phone
        return tenantRepository.save(tenant)
    }

}