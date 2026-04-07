package com.pitercoding.backend.services.admin.reservation;

import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.entity.Reservation;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.enums.UserRole;
import com.pitercoding.backend.repository.ReservationRepository;
import com.pitercoding.backend.repository.RoomRepository;
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

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;

    @Test
    @DisplayName("Get all reservations returns mapped reservations when page has results")
    void getAllReservations_ReturnsMappedReservations_WhenPageHasResults() {
        User user = createUser();
        Room room = createRoom();
        Reservation reservation = createReservation(user, room);
        Page<Reservation> reservationPage = new PageImpl<>(
                List.of(reservation), PageRequest.of(0, 4), 1
        );

        when(reservationRepository.findAll(PageRequest.of(0, 4))).thenReturn(reservationPage);

        ReservationResponseDTO response = reservationServiceImpl.getAllReservations(0);

        assertThat(response).isNotNull();
        assertThat(response.getReservationDtoList()).hasSize(1);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getReservationDtoList().get(0).getId()).isEqualTo(reservation.getId());
        assertThat(response.getReservationDtoList().get(0).getUserId()).isEqualTo(user.getId());
        assertThat(response.getReservationDtoList().get(0).getUserName()).isEqualTo(user.getName());
        assertThat(response.getReservationDtoList().get(0).getRoomId()).isEqualTo(room.getId());
        assertThat(response.getReservationDtoList().get(0).getRoomName()).isEqualTo(room.getName());
        assertThat(response.getReservationDtoList().get(0).getRoomType()).isEqualTo(room.getType());
        assertThat(response.getReservationDtoList().get(0).getPrice()).isEqualTo(reservation.getPrice());
        assertThat(response.getReservationDtoList().get(0).getCheckInDate()).isEqualTo(reservation.getCheckInDate());
        assertThat(response.getReservationDtoList().get(0).getCheckOutDate()).isEqualTo(reservation.getCheckOutDate());
        assertThat(response.getReservationDtoList().get(0).getReservationStatus())
                .isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("Get all reservations returns empty list when page has no results")
    void getAllReservations_ReturnsEmptyList_WhenPageHasNoResults() {
        Page<Reservation> reservationPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 4), 0
        );

        when(reservationRepository.findAll(PageRequest.of(0, 4))).thenReturn(reservationPage);

        ReservationResponseDTO response = reservationServiceImpl.getAllReservations(0);

        assertThat(response).isNotNull();
        assertThat(response.getReservationDtoList()).isEmpty();
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("Change reservation status returns true and approves reservation when status is APPROVED")
    void changeReservationStatus_ReturnsTrueAndApprovesReservation_WhenStatusIsApproved() {
        User user = createUser();
        Room room = createRoom();
        Reservation reservation = createReservation(user, room);

        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        boolean result = reservationServiceImpl.changeReservationStatus(
                reservation.getId(), ReservationStatus.APPROVED.name()
        );

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);

        verify(reservationRepository).save(reservationCaptor.capture());
        verify(roomRepository).save(roomCaptor.capture());

        Reservation savedReservation = reservationCaptor.getValue();
        Room savedRoom = roomCaptor.getValue();

        assertThat(result).isTrue();
        assertThat(savedReservation).isSameAs(reservation);
        assertThat(savedReservation.getReservationStatus()).isEqualTo(ReservationStatus.APPROVED);
        assertThat(savedRoom).isSameAs(room);
        assertThat(savedRoom.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Change reservation status returns true and rejects reservation when status is REJECTED")
    void changeReservationStatus_ReturnsTrueAndRejectsReservation_WhenStatusIsRejected() {
        User user = createUser();
        Room room = createRoom();
        Reservation reservation = createReservation(user, room);

        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        boolean result = reservationServiceImpl.changeReservationStatus(
                reservation.getId(), ReservationStatus.REJECTED.name()
        );

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);

        verify(reservationRepository).save(reservationCaptor.capture());
        verify(roomRepository).save(roomCaptor.capture());

        Reservation savedReservation = reservationCaptor.getValue();
        Room savedRoom = roomCaptor.getValue();

        assertThat(result).isTrue();
        assertThat(savedReservation).isSameAs(reservation);
        assertThat(savedReservation.getReservationStatus()).isEqualTo(ReservationStatus.REJECTED);
        assertThat(savedRoom).isSameAs(room);
        assertThat(savedRoom.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Change reservation status returns false when reservation does not exist")
    void changeReservationStatus_ReturnsFalse_WhenReservationDoesNotExist() {
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        boolean result = reservationServiceImpl.changeReservationStatus(
                reservationId, ReservationStatus.APPROVED.name()
        );

        assertThat(result).isFalse();
        verify(reservationRepository).findById(reservationId);
        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Change reservation status returns false when status is invalid")
    void changeReservationStatus_ReturnsFalse_WhenStatusIsInvalid() {
        User user = createUser();
        Room room = createRoom();
        Reservation reservation = createReservation(user, room);

        when(reservationRepository.findById(reservation.getId()))
                .thenReturn(Optional.of(reservation));

        boolean result = reservationServiceImpl.changeReservationStatus(
                reservation.getId(), "INVALID"
        );

        assertThat(result).isFalse();
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(room.isAvailable()).isTrue();

        verify(reservationRepository).findById(reservation.getId());
        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(roomRepository, never()).save(any(Room.class));
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Admin");
        user.setEmail("admin@test.com");
        user.setPassword("123456");
        user.setUserRole(UserRole.ADMIN);
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

    private Reservation createReservation(User user, Room room) {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.of(2026, 4, 7));
        reservation.setCheckOutDate(LocalDate.of(2026, 4, 9));
        reservation.setPrice(1000L);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservation;
    }
}
