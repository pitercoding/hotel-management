package com.pitercoding.backend.services.customer.room;

import com.pitercoding.backend.dto.RoomsResponseDTO;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomServiceImpl;

    @Test
    @DisplayName("Get available rooms returns mapped rooms when page has results")
    void getAvailableRooms_ReturnsMappedRooms_WhenPageHasResults() {
        Room room1 = createRoom(1L, "Room 1", "Studio", 500L);
        Room room2 = createRoom(2L, "Room 2", "Single", 300L);

        Page<Room> roomsPage = new PageImpl<>(
                List.of(room1, room2), PageRequest.of(0, 6), 2
        );

        when(roomRepository.findByAvailable(true, PageRequest.of(0, 6))).thenReturn(roomsPage);

        RoomsResponseDTO response = roomServiceImpl.getAvailableRooms(0);

        assertThat(response).isNotNull();
        assertThat(response.getRoomDtoList()).hasSize(2);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getRoomDtoList().get(0).getId()).isEqualTo(room1.getId());
        assertThat(response.getRoomDtoList().get(0).getName()).isEqualTo(room1.getName());
        assertThat(response.getRoomDtoList().get(0).getType()).isEqualTo(room1.getType());
        assertThat(response.getRoomDtoList().get(0).getPrice()).isEqualTo(room1.getPrice());
        assertThat(response.getRoomDtoList().get(0).isAvailable()).isEqualTo(room1.isAvailable());
        assertThat(response.getRoomDtoList().get(1).getId()).isEqualTo(room2.getId());
        assertThat(response.getRoomDtoList().get(1).getName()).isEqualTo(room2.getName());
        assertThat(response.getRoomDtoList().get(1).getType()).isEqualTo(room2.getType());
        assertThat(response.getRoomDtoList().get(1).getPrice()).isEqualTo(room2.getPrice());
        assertThat(response.getRoomDtoList().get(1).isAvailable()).isEqualTo(room2.isAvailable());
        verify(roomRepository).findByAvailable(true, PageRequest.of(0, 6));
    }

    @Test
    @DisplayName("Get available rooms returns empty list when no rooms are available")
    void getAvailableRooms_ReturnsEmptyList_WhenNoRoomsAreAvailable() {
        Page<Room> roomsPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 6), 0
        );

        when(roomRepository.findByAvailable(true, PageRequest.of(0, 6))).thenReturn(roomsPage);

        RoomsResponseDTO response = roomServiceImpl.getAvailableRooms(0);

        assertThat(response).isNotNull();
        assertThat(response.getRoomDtoList()).isEmpty();
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
        verify(roomRepository).findByAvailable(true, PageRequest.of(0, 6));
    }

    private Room createRoom(Long id, String name, String type, Long price) {
        Room room = new Room();
        room.setId(id);
        room.setName(name);
        room.setType(type);
        room.setPrice(price);
        room.setAvailable(true);
        return room;
    }
}
