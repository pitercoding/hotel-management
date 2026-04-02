package com.pitercoding.backend.services.customer.booking;

import com.pitercoding.backend.dto.ReservationDTO;

public interface BookingService {

    boolean postReservation(ReservationDTO reservationDTO);
}
