package com.pitercoding.backend.entity;

import com.pitercoding.backend.dto.RoomDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private Long price;
    private boolean available;

    public RoomDTO getRoomDto() {
        RoomDTO roomDTO = new RoomDTO();

        roomDTO.setId(id);
        roomDTO.setName(name);
        roomDTO.setType(type);
        roomDTO.setAvailable(available);
        roomDTO.setPrice(price);

        return roomDTO;
    }
}
