package com.boardinghouse.backend.properties.service

import com.boardinghouse.backend.properties.web.*
import com.boardinghouse.backend.properties.persistence.*
import com.boardinghouse.backend.shared.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PropertyService(
    private val repository: PropertyRepository,
) {
    @Transactional
    fun createProperty(request: CreatePropertyRequest): Property = 
    repository.save(request.toEntity())

    fun findAllActiveProperties(): List<Property> = repository.findByActiveTrue()
    fun findPropertyById(id: Long): Property = 
    repository.findById(id).orElseThrow { NotFoundException("Property with id $id not found") }

    @Transactional
    fun updateProperty(id: Long, request: UpdatePropertyRequest): Property {
        val property = findPropertyById(id)
        property.name = request.name
        property.location = request.location
        return repository.save(property)
    }

    @Transactional
    fun deactivateProperty(id: Long): Property {
        val property = findPropertyById(id)
        property.active = false
        return repository.save(property)
    }
}