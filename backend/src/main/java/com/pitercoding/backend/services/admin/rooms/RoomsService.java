package com.pitercoding.backend.services.admin.rooms;

import com.pitercoding.backend.dto.RoomDTO;
import com.pitercoding.backend.dto.RoomsResponseDTO;

public interface RoomsService {

    boolean postRoom(RoomDTO roomDTO);

    RoomsResponseDTO getAllRooms(int pageNumber);

    RoomDTO getRoomById(Long id);
}