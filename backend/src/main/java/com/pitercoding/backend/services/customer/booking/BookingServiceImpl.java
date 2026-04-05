package com.pitercoding.backend.services.customer.booking;

import com.pitercoding.backend.dto.ReservationDTO;
import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.entity.Reservation;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.repository.ReservationRepository;
import com.pitercoding.backend.repository.RoomRepository;
import com.pitercoding.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    public static final int SEARCH_RESULT_PER_PAGE = 4;

    public boolean postReservation(ReservationDTO reservationDTO) {
        Optional<User> optionalUser = userRepository.findById(reservationDTO.getUserId());
        Optional<Room> optionalRoom = roomRepository.findById(reservationDTO.getRoomId());

        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            Reservation reservation = new Reservation();

            reservation.setRoom(optionalRoom.get());
            reservation.setUser(optionalUser.get());
            reservation.setCheckInDate(reservationDTO.getCheckInDate());
            reservation.setCheckOutDate(reservationDTO.getCheckOutDate());
            reservation.setReservationStatus(ReservationStatus.PENDING);

            Long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
            reservation.setPrice(optionalRoom.get().getPrice() * days);

            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }

    public ReservationResponseDTO getAllReservationsByUserId(Long userId, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, SEARCH_RESULT_PER_PAGE);

        Page<Reservation> reservationPage = reservationRepository.findAllByUserId(pageable, userId);

        ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO();

        reservationResponseDTO.setReservationDtoList(reservationPage.stream()
                .map(Reservation::getReservationDto)
                .collect(Collectors.toList()));

        reservationResponseDTO.setPageNumber(reservationPage.getPageable().getPageNumber());
        reservationResponseDTO.setTotalPages(reservationPage.getTotalPages());

        return reservationResponseDTO;
    }
}
