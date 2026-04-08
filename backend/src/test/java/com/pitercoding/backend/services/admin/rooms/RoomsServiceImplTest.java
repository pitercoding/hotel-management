package com.pitercoding.backend.services.admin.rooms;

import com.pitercoding.backend.dto.RoomDTO;
import com.pitercoding.backend.dto.RoomsResponseDTO;
import com.pitercoding.backend.entity.Room;
import com.pitercoding.backend.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomsServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomsServiceImpl roomsServiceImpl;

    @Test
    @DisplayName("Post room returns true when room is saved successfully")
    void postRoom_ReturnsTrue_WhenRoomIsSavedSuccessfully() {
        RoomDTO roomDTO = createRoomDTO();

        boolean result = roomsServiceImpl.postRoom(roomDTO);

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(roomCaptor.capture());

        Room savedRoom = roomCaptor.getValue();

        assertThat(result).isTrue();
        assertThat(savedRoom.getName()).isEqualTo(roomDTO.getName());
        assertThat(savedRoom.getType()).isEqualTo(roomDTO.getType());
        assertThat(savedRoom.getPrice()).isEqualTo(roomDTO.getPrice());
        assertThat(savedRoom.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Post room returns false when repository throws exception")
    void postRoom_ReturnsFalse_WhenRepositoryThrowsException() {
        RoomDTO roomDTO = createRoomDTO();

        doThrow(new RuntimeException("Database error")).when(roomRepository).save(org.mockito.ArgumentMatchers.any(Room.class));

        boolean result = roomsServiceImpl.postRoom(roomDTO);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Get all rooms returns mapped rooms when page has results")
    void getAllRooms_ReturnsMappedRooms_WhenPageHasResults() {
        Room room1 = createRoom(1L, "Room 1", "Single", 350L, true);
        Room room2 = createRoom(2L, "Room 2", "Studio", 400L, true);

        Page<Room> roomsPage = new PageImpl<>(
                List.of(room1, room2), PageRequest.of(0, 6), 2
        );

        when(roomRepository.findAll(PageRequest.of(0, 6))).thenReturn(roomsPage);

        RoomsResponseDTO response = roomsServiceImpl.getAllRooms(0);

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
        verify(roomRepository).findAll(PageRequest.of(0, 6));
    }

    @Test
    @DisplayName("Get all rooms returns empty list when page has no results")
    void getAllRooms_ReturnsEmptyList_WhenPageHasNoResults() {

        Page<Room> roomsPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 6), 0
        );

        when(roomRepository.findAll(PageRequest.of(0, 6))).thenReturn(roomsPage);

        RoomsResponseDTO response = roomsServiceImpl.getAllRooms(0);

        assertThat(response).isNotNull();
        assertThat(response.getRoomDtoList()).isEmpty();
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
        verify(roomRepository).findAll(PageRequest.of(0, 6));
    }

    @Test
    @DisplayName("Get room by id returns mapped room when room exists")
    void getRoomById_ReturnsMappedRoom_WhenRoomExists() {
        Room room1 = createRoom(1L, "Room 1", "Single", 350L, true);

        when(roomRepository.findById(room1.getId())).thenReturn(Optional.of(room1));

        RoomDTO response = roomsServiceImpl.getRoomById(room1.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(room1.getId());
        assertThat(response.getName()).isEqualTo(room1.getName());
        assertThat(response.getType()).isEqualTo(room1.getType());
        assertThat(response.getPrice()).isEqualTo(room1.getPrice());
        assertThat(response.isAvailable()).isEqualTo(room1.isAvailable());
        verify(roomRepository).findById(room1.getId());
    }

    @Test
    @DisplayName("Get room by id throws entity not found exception when room does not exist")
    void getRoomById_ThrowsEntityNotFoundException_WhenRoomDoesNotExist() {
        Long id = 100L;

        when(roomRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomsServiceImpl.getRoomById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Room with id " + id + " not found");

        verify(roomRepository).findById(id);
    }

    @Test
    @DisplayName("Update room returns true when room exists")
    void updateRoom_ReturnsTrue_WhenRoomExists() {
        Room existingRoom = createRoom(1L, "Room 1", "Single", 350L, true);
        RoomDTO roomDTO = createRoomDTO();

        when(roomRepository.findById(existingRoom.getId())).thenReturn(Optional.of(existingRoom));

        boolean result = roomsServiceImpl.updateRoom(1L, roomDTO);

        assertThat(result).isTrue();
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).findById(existingRoom.getId());
        verify(roomRepository).save(roomCaptor.capture());

        Room savedRoom = roomCaptor.getValue();

        assertThat(savedRoom).isSameAs(existingRoom);
        assertThat(savedRoom.getName()).isEqualTo(roomDTO.getName());
        assertThat(savedRoom.getType()).isEqualTo(roomDTO.getType());
        assertThat(savedRoom.getPrice()).isEqualTo(roomDTO.getPrice());
        assertThat(savedRoom.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Update room returns false when room does not exist")
    void updateRoom_ReturnsFalse_WhenRoomDoesNotExist() {
        Long id = 100L;
        RoomDTO roomDTO = createRoomDTO();

        when(roomRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = roomsServiceImpl.updateRoom(id, roomDTO);

        assertThat(result).isFalse();
        verify(roomRepository).findById(id);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Delete room deletes room when room exists")
    void deleteRoom_DeletesRoom_WhenRoomExists() {
        Room room = createRoom(1L, "Room 1", "Single", 350L, true);

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        roomsServiceImpl.deleteRoom(room.getId());

        verify(roomRepository).findById(room.getId());
        verify(roomRepository).deleteById(room.getId());
    }

    @Test
    @DisplayName("Delete room throws entity not found exception when room does not exist")
    void deleteRoom_ThrowsEntityNotFoundException_WhenRoomDoesNotExist() {
        Long id = 100L;

        when(roomRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomsServiceImpl.deleteRoom(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Room with id " + id + " not found");

        verify(roomRepository).findById(id);
        verify(roomRepository, never()).deleteById(any(Long.class));
    }

    private Room createRoom(
            Long id,
            String name,
            String type,
            Long price,
            boolean available) {
        Room room = new Room();
        room.setId(id);
        room.setName(name);
        room.setType(type);
        room.setPrice(price);
        room.setAvailable(available);
        return room;
    }

    private RoomDTO createRoomDTO() {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("Room 2");
        roomDTO.setType("Single");
        roomDTO.setPrice(400L);
        roomDTO.setAvailable(true);
        return roomDTO;
    }
}
