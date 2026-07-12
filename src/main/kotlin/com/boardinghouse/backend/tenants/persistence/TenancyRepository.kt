package com.boardinghouse.backend.tenants.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface TenancyRepository: JpaRepository<Tenancy, Long> {
    fun existsByRoomIdAndEndDateIsNull(roomId: Long): Boolean
}