package com.pitercoding.backend.services.admin.reservation;

import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.entity.Reservation;
import com.pitercoding.backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    public static final int SEARCH_RESULT_PER_PAGE = 4;

    public ReservationResponseDTO getAllReservations(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, SEARCH_RESULT_PER_PAGE);

        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);

        ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

        reservationResponseDTO.setReservationDtoList(reservationPage.stream()
                .map(Reservation::getReservationDto)
                .collect(Collectors.toList()));

        reservationResponseDTO.setPageNumber(reservationPage.getPageable().getPageNumber());
        reservationResponseDTO.setTotalPages(reservationPage.getTotalPages());

        return reservationResponseDTO;


    }
}
