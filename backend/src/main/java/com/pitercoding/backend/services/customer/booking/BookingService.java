package com.pitercoding.backend.services.customer.booking;

import com.pitercoding.backend.dto.ReservationDTO;
import com.pitercoding.backend.dto.ReservationResponseDTO;

public interface BookingService {

    boolean postReservation(ReservationDTO reservationDTO);
    ReservationResponseDTO getAllReservationsByUserId(Long userId, int pageNumber);
}
