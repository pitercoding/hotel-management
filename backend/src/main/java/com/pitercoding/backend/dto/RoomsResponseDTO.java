package com.pitercoding.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomsResponseDTO {

    private List<RoomDTO> roomDtoList;
    private Integer totalPages;
    private Integer pageNumber;

}
