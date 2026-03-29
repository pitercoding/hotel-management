package com.pitercoding.backend.services.admin.rooms;

import com.pitercoding.backend.dto.RoomDTO;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
