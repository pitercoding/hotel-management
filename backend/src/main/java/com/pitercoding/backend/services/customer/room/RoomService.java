package com.pitercoding.backend.services.customer.room;

import com.pitercoding.backend.dto.RoomsResponseDTO;

public interface RoomService {

    RoomsResponseDTO getAvailableRooms(int pageNumber);
}
