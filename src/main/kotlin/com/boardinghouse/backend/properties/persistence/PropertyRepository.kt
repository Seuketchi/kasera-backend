package com.boardinghouse.backend.properties.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface PropertyRepository : JpaRepository<Property, Long> {
    fun findByActiveTrue(): List<Property>

}