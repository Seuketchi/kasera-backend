package com.boardinghouse.backend.rooms.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<Room, Long> {
    fun findByActiveTrue(): List<Room>
}