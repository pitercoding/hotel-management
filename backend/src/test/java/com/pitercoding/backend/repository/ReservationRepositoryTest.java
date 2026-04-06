package com.pitercoding.backend.repository;

import com.pitercoding.backend.entity.Reservation;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("Find all by user id returns reservations by user id when successful")
    void findAllByUserId_ReturnsReservationsByUserId_WhenSuccessful() {
        User user = createUser();
        userRepository.save(user);

        Room room = createRoom();
        roomRepository.save(room);

        Reservation reservation = createReservation(user, room);
        reservationRepository.save(reservation);

        Page<Reservation> reservationPage = reservationRepository.findAllByUserId(PageRequest.of(0, 10), user.getId());

        Reservation foundReservation = reservationPage.getContent().get(0);

        assertThat(reservationPage).isNotNull();
        assertThat(reservationPage.getContent()).hasSize(1);
        assertThat(foundReservation.getUser().getId()).isEqualTo(user.getId());
        assertThat(foundReservation.getRoom().getId()).isEqualTo(room.getId());
    }

    @Test
    @DisplayName("Find all by user id returns empty page when user has no reservations")
    void findAllByUserId_ReturnsEmptyPage_WhenUserHasNoReservations() {
        User user = createUser();
        userRepository.save(user);

        Page<Reservation> reservationPage =
                reservationRepository.findAllByUserId(PageRequest.of(0, 10), user.getId());

        assertThat(reservationPage).isNotNull();
        assertThat(reservationPage.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Find all by user id returns only reservations from requested user when successful")
    void findAllByUserId_ReturnsOnlyReservationsFromRequestedUser_WhenSuccessful() {
        User user01 = createUser();
        userRepository.save(user01);

        User user02 = createAnotherUser();
        userRepository.save(user02);

        Room room01 = createRoom();
        roomRepository.save(room01);

        Room room02 = createAnotherRoom();
        roomRepository.save(room02);

        Reservation reservation01 = createReservation(user01, room01);
        reservationRepository.save(reservation01);

        Reservation reservation02 = createReservation(user02, room02);
        reservationRepository.save(reservation02);

        Page<Reservation> reservationPage =
                reservationRepository.findAllByUserId(PageRequest.of(0, 10), user01.getId());

        Reservation foundReservation = reservationPage.getContent().get(0);

        assertThat(reservationPage).isNotNull();
        assertThat(reservationPage.getContent()).hasSize(1);
        assertThat(foundReservation.getUser().getId()).isEqualTo(user01.getId());
        assertThat(foundReservation.getRoom().getId()).isEqualTo(room01.getId());
    }

    private User createUser() {
        User user = new User();
        user.setEmail("customer01@test.com");
        user.setPassword("customer");
        user.setName("Customer 01");
        user.setUserRole(UserRole.CUSTOMER);
        return user;
    }

    private User createAnotherUser() {
        User user = new User();
        user.setEmail("customer02@test.com");
        user.setPassword("customer");
        user.setName("Customer 02");
        user.setUserRole(UserRole.CUSTOMER);
        return user;
    }

    private Room createRoom() {
        Room room = new Room();
        room.setName("Room 01");
        room.setType("Studio");
        room.setPrice(500L);
        room.setAvailable(true);
        return room;
    }

    private Room createAnotherRoom() {
        Room room = new Room();
        room.setName("Room 02");
        room.setType("Single");
        room.setPrice(400L);
        room.setAvailable(true);
        return room;
    }

    private Reservation createReservation(User user, Room room) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(2));
        reservation.setPrice(1000L);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        return reservation;
    }
}