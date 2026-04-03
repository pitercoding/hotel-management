package com.pitercoding.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReservationResponseDTO {

    private Integer totalPages;
    private Integer pageNumber;
    private List<ReservationDTO> reservationDtoList;
}
