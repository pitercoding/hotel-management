package com.pitercoding.backend.services.admin.reservation;

import com.pitercoding.backend.dto.ReservationResponseDTO;

public interface ReservationService {

    ReservationResponseDTO getAllReservations(int pageNumber);
    boolean changeReservationStatus(Long reservationId, String reservationStatus);
}
