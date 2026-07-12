package com.boardinghouse.backend.rooms.service

import com.boardinghouse.backend.rooms.persistence.*
import com.boardinghouse.backend.rooms.web.*
import com.boardinghouse.backend.properties.persistence.PropertyRepository
import com.boardinghouse.backend.shared.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val propertyRepository: PropertyRepository
) {

    fun findAllActiveRooms(): List<Room> {
        return roomRepository.findByActiveTrue()
    }
    fun findRoomById(id: Long): Room {
        return roomRepository.findById(id).orElseThrow(
            { NotFoundException("Room with id $id not found") }
        )
    }

    @Transactional
    fun createRoom(propertyId: Long, request: CreateRoomRequest): Room {
        if(!propertyRepository.existsById(propertyId)) {
            throw NotFoundException("Property with id $propertyId not found")
        }
        return roomRepository.save(request.toEntity(propertyId))
    }

    @Transactional
    fun updateRoom(id: Long, request: UpdateRoomRequest): Room {
        val room = findRoomById(id)
        room.label = request.label
        room.monthlyRent = request.monthlyRent
        room.description = request.description
        return roomRepository.save(room)
    }

    @Transactional
    fun deactivateRoom(id: Long): Room {
        val room = findRoomById(id)
        room.active = false
        return roomRepository.save(room)
    }
}