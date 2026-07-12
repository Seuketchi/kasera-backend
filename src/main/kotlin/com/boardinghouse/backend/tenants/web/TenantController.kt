package com.boardinghouse.backend.tenants.web

import com.boardinghouse.backend.tenants.service.TenantService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tenants")
class TenantController (
    private val tenantService: TenantService,
){
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody tenant: CreateTenantRequest) : TenantResponse =
        tenantService.createTenant(tenant).toResponse()

    @GetMapping
    fun findAll(): List<TenantResponse> =
        tenantService.findAllTenants().map { it.toResponse() }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): TenantResponse =
        tenantService.findTenantById(id).toResponse()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody tenant: UpdateTenantRequest): TenantResponse =
        tenantService.updateTenant(id, tenant).toResponse()
}