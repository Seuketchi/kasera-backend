package com.boardinghouse.backend.rooms.web

import com.boardinghouse.backend.rooms.service.RoomService
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val roomService: RoomService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)                                             //201
    fun create(@RequestParam propertyId: Long, @RequestBody request: CreateRoomRequest): RoomResponse =
        roomService.createRoom(propertyId, request).toResponse()

    @GetMapping                                                                     //200
    fun findAllActive(): List<RoomResponse> =
        roomService.findAllActiveRooms().map { it.toResponse() }

    @GetMapping("/{id}")                                                           //200
    fun findById(@PathVariable id: Long): RoomResponse =
        roomService.findRoomById(id).toResponse()

    @PutMapping("/{id}")                                                           //200
    fun update(@PathVariable id: Long, @RequestBody request: UpdateRoomRequest): RoomResponse =
        roomService.updateRoom(id, request).toResponse()

    @PostMapping("/{id}/deactivate")                                               //200
    fun deactivate(@PathVariable id: Long): RoomResponse =
        roomService.deactivateRoom(id).toResponse()
}