package com.boardinghouse.backend.tenants.web

import com.boardinghouse.backend.tenants.persistence.Tenancy
import com.boardinghouse.backend.tenants.service.TenancyService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/tenancies")
class TenancyController (
    private val tenancyService: TenancyService
){
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestParam roomId: Long, @RequestBody request: CreateTenancyRequest): TenancyResponse =
        tenancyService.createTenancy(roomId, request).toResponse()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): TenancyResponse =
        tenancyService.findTenancyById(id).toResponse()

    @PostMapping("/{id}/end")
    fun end(@PathVariable id: Long, @RequestBody request: EndTenancyRequest): TenancyResponse =
        tenancyService.endTenancy(id, request).toResponse()

}