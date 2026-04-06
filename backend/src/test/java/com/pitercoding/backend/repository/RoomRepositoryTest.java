package com.pitercoding.backend.repository;

import com.pitercoding.backend.entity.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("Find by available returns available room when successful")
    void findByAvailable_ReturnsAvailableRoom_WhenSuccessful() {
        Room room = createAvailableRoom();
        roomRepository.save(room);

        Page<Room> roomPage = roomRepository.findByAvailable(true, PageRequest.of(0, 10));
        Room foundRoom = roomPage.getContent().get(0);

        assertThat(roomPage).isNotNull();
        assertThat(roomPage.getContent()).hasSize(1);
        assertThat(foundRoom.isAvailable()).isTrue();
        assertThat(foundRoom.getName()).isEqualTo(room.getName());
    }

    @Test
    @DisplayName("Find by available returns empty when room is not available")
    void findByAvailable_ReturnsEmpty_WhenRoomIsNotAvailable() {
        Room room = createNotAvailableRoom();
        roomRepository.save(room);

        Page<Room> roomPage = roomRepository.findByAvailable(true, PageRequest.of(0, 10));

        assertThat(roomPage).isNotNull();
        assertThat(roomPage.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Find by available returns only available rooms when successful")
    void findByAvailable_ReturnsOnlyAvailableRooms_WhenSuccessful() {
        Room availableRoom = createAvailableRoom();
        roomRepository.save(availableRoom);

        Room notAvailableRoom = createNotAvailableRoom();
        roomRepository.save(notAvailableRoom);

        Page<Room> roomPage = roomRepository.findByAvailable(true, PageRequest.of(0, 10));

        Room foundRoom = roomPage.getContent().get(0);

        assertThat(roomPage).isNotNull();
        assertThat(roomPage.getContent()).hasSize(1);
        assertThat(foundRoom.isAvailable()).isTrue();
        assertThat(foundRoom.getName()).isEqualTo(availableRoom.getName());
    }

    private Room createAvailableRoom() {
        Room room = new Room();
        room.setName("Room 1");
        room.setType("Studio");
        room.setPrice(500L);
        room.setAvailable(true);
        return room;
    }

    private Room createNotAvailableRoom() {
        Room room = new Room();
        room.setName("Room 2");
        room.setType("Studio");
        room.setPrice(500L);
        room.setAvailable(false);
        return room;
    }
}