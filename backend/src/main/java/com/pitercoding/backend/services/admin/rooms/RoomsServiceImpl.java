package com.pitercoding.backend.services.admin.rooms;

import com.pitercoding.backend.dto.RoomDTO;
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
public class RoomsServiceImpl implements RoomsService {

    private final RoomRepository roomRepository;

    public boolean postRoom(RoomDTO roomDTO) {
        try{
            Room room = new Room();

            room.setName(roomDTO.getName());
            room.setPrice(roomDTO.getPrice());
            room.setType(roomDTO.getType());
            room.setAvailable(true);

            roomRepository.save(room);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public RoomsResponseDTO getAllRooms(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, 1);
        Page<Room> roomPage = roomRepository.findAll(pageable);

        RoomsResponseDTO roomsResponseDTO = new RoomsResponseDTO();
        roomsResponseDTO.setPageNumber(roomPage.getPageable().getPageNumber());
        roomsResponseDTO.setTotalPages(roomPage.getTotalPages());
        roomsResponseDTO.setRoomDtoList(roomPage.stream().map(Room::getRoomDto).collect(Collectors.toList()));

        return roomsResponseDTO;
    }
}
