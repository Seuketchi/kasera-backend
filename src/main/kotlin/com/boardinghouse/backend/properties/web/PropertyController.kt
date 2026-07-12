package com.boardinghouse.backend.properties.web

import com.boardinghouse.backend.properties.service.PropertyService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/properties")
class PropertyController(
    private val propertyService: PropertyService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)                                             //201
    fun create(@RequestBody request: CreatePropertyRequest): PropertyResponse =
        propertyService.createProperty(request).toResponse()

    @GetMapping                                                                     //200
    fun findAllActive(): List<PropertyResponse> =
        propertyService.findAllActiveProperties().map { it.toResponse() }

    @GetMapping("/{id}")                                                           //200
    fun findById(@PathVariable id: Long): PropertyResponse =
        propertyService.findPropertyById(id).toResponse()

    @PutMapping("/{id}")                                                           //200
    fun update(@PathVariable id: Long, @RequestBody request: UpdatePropertyRequest): PropertyResponse =
        propertyService.updateProperty(id, request).toResponse()

    @PostMapping("/{id}/deactivate")                                               //200
    fun deactivate(@PathVariable id: Long): PropertyResponse =
        propertyService.deactivateProperty(id).toResponse()
}