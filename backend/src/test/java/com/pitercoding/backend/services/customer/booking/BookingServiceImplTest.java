package com.pitercoding.backend.services.customer.booking;

import com.pitercoding.backend.dto.ReservationDTO;
import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.entity.Reservation;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.enums.UserRole;
import com.pitercoding.backend.repository.ReservationRepository;
import com.pitercoding.backend.repository.RoomRepository;
import com.pitercoding.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class done
{

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Post reservation returns true when successful")
    void postReservation_ReturnsTrue_WhenSuccessful() {
        User user = createUser();
        Room room = createRoom();
        ReservationDTO reservationDTO = createReservationDTO(user.getId(), room.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        boolean result = bookingService.postReservation(reservationDTO);

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationCaptor.capture());

        Reservation savedReservation = reservationCaptor.getValue();

        assertThat(result).isTrue();
        assertThat(savedReservation.getUser()).isEqualTo(user);
        assertThat(savedReservation.getRoom()).isEqualTo(room);
        assertThat(savedReservation.getReservationStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(savedReservation.getPrice()).isEqualTo(1000L);
        assertThat(savedReservation.getCheckInDate()).isEqualTo(reservationDTO.getCheckInDate());
        assertThat(savedReservation.getCheckOutDate()).isEqualTo(reservationDTO.getCheckOutDate());
    }

    @Test
    @DisplayName("Post reservation calculates price based on date range when successful")
    void postReservation_CalculatesPriceBasedOnDateRange_WhenSuccessful() {
        User user = createUser();
        Room room = createRoom();
        ReservationDTO reservationDTO = createReservationDTO(user.getId(), room.getId());
        reservationDTO.setCheckInDate(LocalDate.of(2026, 4, 7));
        reservationDTO.setCheckOutDate(LocalDate.of(2026, 4, 10));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        boolean result = bookingService.postReservation(reservationDTO);

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationCaptor.capture());

        Reservation savedReservation = reservationCaptor.getValue();

        assertThat(result).isTrue();
        assertThat(savedReservation.getPrice()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("Post reservation returns false when user does not exist")
    void postReservation_ReturnsFalse_WhenUserDoesNotExist() {
        Room room = createRoom();
        ReservationDTO reservationDTO = createReservationDTO(1L, room.getId());

        when(userRepository.findById(reservationDTO.getUserId())).thenReturn(Optional.empty());
        when(roomRepository.findById(reservationDTO.getRoomId())).thenReturn(Optional.of(room));

        boolean result = bookingService.postReservation(reservationDTO);

        assertThat(result).isFalse();
        verify(userRepository).findById(reservationDTO.getUserId());
        verify(roomRepository).findById(reservationDTO.getRoomId());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Post reservation returns false when room does not exist")
    void postReservation_ReturnsFalse_WhenRoomDoesNotExist() {
        User user = createUser();
        Long missingRoomId = 99L;
        ReservationDTO reservationDTO = createReservationDTO(user.getId(), missingRoomId);

        when(userRepository.findById(reservationDTO.getUserId())).thenReturn(Optional.of(user));
        when(roomRepository.findById(reservationDTO.getRoomId())).thenReturn(Optional.empty());

        boolean result = bookingService.postReservation(reservationDTO);

        assertThat(result).isFalse();
        verify(userRepository).findById(reservationDTO.getUserId());
        verify(roomRepository).findById(reservationDTO.getRoomId());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Get all reservations by user id returns reservations when successful")
    void getAllReservationsByUserId_ReturnsReservations_WhenSuccessful() {
        User user = createUser();
        Room room = createRoom();
        Reservation reservation = createReservation(user, room);
        Page<Reservation> reservationPage = new PageImpl<>(
                List.of(reservation), PageRequest.of(0, 4), 1
        );

        when(reservationRepository.findAllByUserId(PageRequest.of(0, 4),
                user.getId())).thenReturn(reservationPage);

        ReservationResponseDTO response = bookingService.getAllReservationsByUserId(user.getId(), 0);

        verify(reservationRepository).findAllByUserId(PageRequest.of(0, 4), user.getId());

        assertThat(response).isNotNull();
        assertThat(response.getReservationDtoList()).hasSize(1);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getReservationDtoList().get(0).getUserId()).isEqualTo(user.getId());
        assertThat(response.getReservationDtoList().get(0).getRoomId()).isEqualTo(room.getId());
        assertThat(response.getReservationDtoList().get(0).getUserName()).isEqualTo(user.getName());
        assertThat(response.getReservationDtoList().get(0).getRoomName()).isEqualTo(room.getName());
        assertThat(response.getReservationDtoList().get(0).getRoomType()).isEqualTo(room.getType());
        assertThat(response.getReservationDtoList().get(0).getReservationStatus())
                .isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("Get all reservations by user id returns empty list when user has no reservations")
    void getAllReservationsByUserId_ReturnsEmptyList_WhenUserHasNoReservations() {
        User user = createUser();
        Page<Reservation> reservationPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 4), 0
        );

        when(reservationRepository.findAllByUserId(PageRequest.of(0, 4),
                user.getId())).thenReturn(reservationPage);

        ReservationResponseDTO response = bookingService.getAllReservationsByUserId(user.getId(), 0);

        verify(reservationRepository).findAllByUserId(PageRequest.of(0, 4), user.getId());

        assertThat(response).isNotNull();
        assertThat(response.getReservationDtoList()).isEmpty();
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Customer");
        user.setEmail("customer@test.com");
        user.setPassword("123456");
        user.setUserRole(UserRole.CUSTOMER);
        return user;
    }

    private Room createRoom() {
        Room room = new Room();
        room.setId(1L);
        room.setName("Room 1");
        room.setType("Studio");
        room.setPrice(500L);
        room.setAvailable(true);
        return room;
    }

    private ReservationDTO createReservationDTO(Long userId, Long roomId) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserId(userId);
        reservationDTO.setRoomId(roomId);
        reservationDTO.setCheckInDate(LocalDate.of(2026, 4, 7));
        reservationDTO.setCheckOutDate(LocalDate.of(2026, 4, 9));
        return reservationDTO;
    }

    private Reservation createReservation(User user, Room room) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.of(2026, 4, 7));
        reservation.setCheckOutDate(LocalDate.of(2026, 4, 9));
        reservation.setPrice(1000L);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservation;
    }
}
