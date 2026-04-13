package com.pitercoding.backend.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pitercoding.backend.configs.JwtAuthenticationFilter;
import com.pitercoding.backend.dto.RoomDTO;
import com.pitercoding.backend.dto.RoomsResponseDTO;
import com.pitercoding.backend.services.admin.rooms.RoomsService;
import com.pitercoding.backend.services.jwt.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomsController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomsService roomsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Post room returns ok when room is created successfully")
    void postRoom_ReturnsOk_WhenRoomIsCreatedSuccessfully() throws Exception {
        RoomDTO roomDTO = createRoomDTO();

        when(roomsService.postRoom(any(RoomDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/admin/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isOk());

        verify(roomsService).postRoom(any(RoomDTO.class));
    }

    @Test
    @DisplayName("Post room returns bad request when room is not created")
    void postRoom_ReturnsBadRequest_WhenRoomIsNotCreated() throws Exception {
        RoomDTO roomDTO = createRoomDTO();

        when(roomsService.postRoom(any(RoomDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/admin/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDTO)))
                .andExpect(status().isBadRequest());

        verify(roomsService).postRoom(any(RoomDTO.class));
    }

    @Test
    @DisplayName("Get all rooms returns rooms when request is successful")
    void getAllRooms_ReturnsRooms_WhenRequestIsSuccessful() throws Exception {
        int pageNumber = 0;
        RoomsResponseDTO roomsResponseDTO = createRoomsResponseDTO();

        when(roomsService.getAllRooms(pageNumber)).thenReturn(roomsResponseDTO);

        RoomDTO room = roomsResponseDTO.getRoomDtoList().get(0);

        mockMvc.perform(get("/api/admin/rooms/{pageNumber}", pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(roomsResponseDTO.getPageNumber()))
                .andExpect(jsonPath("$.totalPages").value(roomsResponseDTO.getTotalPages()))
                .andExpect(jsonPath("$.roomDtoList.length()").value(1))
                .andExpect(jsonPath("$.roomDtoList[0].id")
                        .value(room.getId()))
                .andExpect(jsonPath("$.roomDtoList[0].name")
                        .value(room.getName()))
                .andExpect(jsonPath("$.roomDtoList[0].type")
                        .value(room.getType()))
                .andExpect(jsonPath("$.roomDtoList[0].price")
                        .value(room.getPrice()))
                .andExpect(jsonPath("$.roomDtoList[0].available")
                        .value(room.isAvailable()));

        verify(roomsService).getAllRooms(pageNumber);
    }

    @Test
    @DisplayName("Get all rooms returns empty list when no room exist")
    void getAllRooms_ReturnsEmptyList_WhenNoRoomsExist() throws Exception {
        RoomsResponseDTO response = new RoomsResponseDTO();
        response.setPageNumber(0);
        response.setTotalPages(0);
        response.setRoomDtoList(List.of());

        when(roomsService.getAllRooms(0)).thenReturn(response);

        mockMvc.perform(get("/api/admin/rooms/{pageNumber}", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.roomDtoList.length()").value(0));

        verify(roomsService).getAllRooms(0);
    }

    @Test
    @DisplayName("Get room by id returns room when room exists")
    void getRoomById_ReturnsRoom_WhenRoomExists() throws Exception {
        Long roomId = 1L;
        RoomDTO room = createRoomDTO();

        when(roomsService.getRoomById(roomId)).thenReturn(room);

        mockMvc.perform(get("/api/admin/room/{id}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(room.getId()))
                .andExpect(jsonPath("$.name")
                        .value(room.getName()))
                .andExpect(jsonPath("$.type")
                        .value(room.getType()))
                .andExpect(jsonPath("$.price")
                        .value(room.getPrice()))
                .andExpect(jsonPath("$.available")
                        .value(room.isAvailable()));

        verify(roomsService).getRoomById(roomId);
    }

    @Test
    @DisplayName("Get room by id returns not found when room does not exist")
    void getRoomById_ReturnsNotFound_WhenRoomDoesNotExist() throws Exception {
        Long roomId = 1L;
        String errorMessage = "Room not found";

        when(roomsService.getRoomById(roomId))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(get("/api/admin/room/{id}", roomId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));

        verify(roomsService).getRoomById(roomId);
    }

    @Test
    @DisplayName("Get room by id returns internal server error when service throws unexpected exception")
    void getRoomById_ReturnsInternalServerError_WhenServiceThrowsUnexpectedException() throws Exception {
        Long roomId = 1L;

        when(roomsService.getRoomById(roomId)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/admin/room/{id}", roomId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong."));

        verify(roomsService).getRoomById(roomId);
    }

    @Test
    @DisplayName("Update room returns ok when room is updated successfully")
    void updateRoom_ReturnsOk_WhenRoomIsUpdatedSuccessfully() throws Exception {
        Long roomId = 1L;
        RoomDTO room = createRoomDTO();

        when(roomsService.updateRoom(eq(roomId), any(RoomDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/admin/room/{id}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isOk());

        verify(roomsService).updateRoom(eq(roomId), any(RoomDTO.class));
    }

    @Test
    @DisplayName("Update room returns not found when room is not updated")
    void updateRoom_ReturnsNotFound_WhenRoomIsNotUpdated() throws Exception {
        Long roomId = 1L;
        RoomDTO room = createRoomDTO();

        when(roomsService.updateRoom(eq(roomId), any(RoomDTO.class))).thenReturn(false);

        mockMvc.perform(put("/api/admin/room/{id}", roomId)
                .content(objectMapper.writeValueAsString(room))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(roomsService).updateRoom(eq(roomId), any(RoomDTO.class));
    }

    @Test
    @DisplayName("Delete room returns no content when room is deleted successfully")
    void deleteRoom_ReturnsNoContent_WhenRoomIsDeletedSuccessfully() throws Exception {
        Long roomId = 1L;

        doNothing().when(roomsService).deleteRoom(roomId);

        mockMvc.perform(delete("/api/admin/room/{id}", roomId))
                .andExpect(status().isNoContent());

        verify(roomsService).deleteRoom(roomId);
    }

    @Test
    @DisplayName("Delete room returns not found when room does not exist")
    void deleteRoom_ReturnsNotFound_WhenRoomDoesNotExist() throws Exception {
        Long roomId = 1L;
        String errorMessage = "Room not found";

        doThrow(new EntityNotFoundException(errorMessage))
                .when(roomsService).deleteRoom(roomId);

        mockMvc.perform(delete("/api/admin/room/{id}", roomId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));

        verify(roomsService).deleteRoom(roomId);
    }

    @Test
    @DisplayName("Delete room returns internal server error when service throws unexpected exception")
    void deleteRoom_ReturnsInternalServerError_WhenServiceThrowsUnexpectedException() throws Exception {
        Long roomId = 1L;

        doThrow(new RuntimeException("Internal server error"))
                .when(roomsService).deleteRoom(roomId);

        mockMvc.perform(delete("/api/admin/room/{id}", roomId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong."));

        verify(roomsService).deleteRoom(roomId);
    }

    private RoomDTO createRoomDTO() {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setName("Room 1");
        roomDTO.setType("Studio");
        roomDTO.setPrice(500L);
        roomDTO.setAvailable(true);
        return roomDTO;
    }

    private RoomsResponseDTO createRoomsResponseDTO() {
        RoomsResponseDTO responseDTO = new RoomsResponseDTO();
        responseDTO.setPageNumber(0);
        responseDTO.setTotalPages(1);
        responseDTO.setRoomDtoList(List.of(createRoomDTO()));
        return responseDTO;
    }
}
