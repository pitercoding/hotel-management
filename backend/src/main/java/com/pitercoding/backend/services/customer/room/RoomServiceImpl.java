package com.pitercoding.backend.services.customer.room;

import com.pitercoding.backend.dto.RoomsResponseDTO;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomsResponseDTO getAvailableRooms(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, 6);
        Page<Room> roomPage = roomRepository.findByAvailable(true, pageable);

        RoomsResponseDTO roomsResponseDTO = new RoomsResponseDTO();
        roomsResponseDTO.setPageNumber(roomPage.getPageable().getPageNumber());
        roomsResponseDTO.setTotalPages(roomPage.getTotalPages());
        roomsResponseDTO.setRoomDtoList(roomPage.stream().map(Room::getRoomDto).collect(Collectors.toList()));

        return roomsResponseDTO;
    }

}
