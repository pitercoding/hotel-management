package com.pitercoding.backend.services.admin.reservation;

import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.entity.Reservation;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.repository.ReservationRepository;
import com.pitercoding.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
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

    public boolean changeReservationStatus(Long reservationId, String reservationStatus) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isPresent()) {
            Reservation existingReservation = optionalReservation.get();

            if (Objects.equals(reservationStatus, ReservationStatus.APPROVED.name())) {
                existingReservation.setReservationStatus(ReservationStatus.APPROVED);
            } else if (Objects.equals(reservationStatus, ReservationStatus.REJECTED.name())) {
                existingReservation.setReservationStatus(ReservationStatus.REJECTED);
            } else {
                return false;
            }
            reservationRepository.save(existingReservation);

            Room existingRoom = existingReservation.getRoom();
            existingRoom.setAvailable(false);
            roomRepository.save(existingRoom);

            return true;
        }
        return false;
    }
}
