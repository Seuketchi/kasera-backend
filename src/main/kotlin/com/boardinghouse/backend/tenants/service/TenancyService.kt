package com.boardinghouse.backend.tenants.service

import com.boardinghouse.backend.rooms.persistence.RoomRepository
import com.boardinghouse.backend.shared.ConflictException
import com.boardinghouse.backend.shared.NotFoundException
import com.boardinghouse.backend.tenants.persistence.Tenancy
import com.boardinghouse.backend.tenants.persistence.TenancyRepository
import com.boardinghouse.backend.tenants.persistence.TenantRepository
import com.boardinghouse.backend.tenants.web.CreateTenancyRequest
import com.boardinghouse.backend.tenants.web.EndTenancyRequest
import jakarta.transaction.Transactional
import org.springframework.stereotype.*


@Service
class TenancyService (
    private val tenancyRepository: TenancyRepository,
    private val roomRepository: RoomRepository,
    private val tenantRepository: TenantRepository,
) {
    @Transactional
    fun createTenancy(roomId: Long, request: CreateTenancyRequest): Tenancy {
        // room must exist
        val room = roomRepository.findById(roomId)
            .orElseThrow { NotFoundException("Room not found with id: $roomId") }

        // tenant must exist
        if (!tenantRepository.existsById(request.tenantId)) {
            throw NotFoundException("Tenant not found with id: ${request.tenantId}")
        }

        // room must be free
        if (tenancyRepository.existsByRoomIdAndEndDateIsNull(roomId)) {
            throw ConflictException("Tenancy already exists with id: ${request.tenantId}")
        }

        val tenancy = Tenancy(
            tenantId = request.tenantId,
            roomId = room.id,
            monthlyRent = room.monthlyRent,
            deposit = request.deposit,
            startDate = request.startDate,
        )
        return tenancyRepository.save(tenancy)
    }

    fun findTenancyById(id: Long): Tenancy =
        tenancyRepository.findById(id)
            .orElseThrow { NotFoundException("Tenancy not found with id: $id") }

    @Transactional
    fun endTenancy(id: Long, request: EndTenancyRequest): Tenancy {
        val tenancy = findTenancyById(id)
        tenancy.endDate = request.endDate
        return tenancyRepository.save(tenancy)
    }
}