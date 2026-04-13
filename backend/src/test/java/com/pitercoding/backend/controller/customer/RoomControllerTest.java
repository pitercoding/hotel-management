package com.pitercoding.backend.controller.customer;

import com.pitercoding.backend.configs.JwtAuthenticationFilter;
import com.pitercoding.backend.dto.RoomDTO;
import com.pitercoding.backend.dto.RoomsResponseDTO;
import com.pitercoding.backend.services.customer.room.RoomService;
import com.pitercoding.backend.services.jwt.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Get available rooms returns rooms when request is successful")
    void getAvailableRooms_ReturnsRooms_WhenRequestIsSuccessful() throws Exception {
        int pageNumber = 0;
        RoomsResponseDTO roomsResponseDTO = createRoomsResponseDTO();
        RoomDTO room = roomsResponseDTO.getRoomDtoList().get(0);

        when(roomService.getAvailableRooms(pageNumber)).thenReturn(roomsResponseDTO);

        mockMvc.perform(get("/api/customer/rooms/{pageNumber}", pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(roomsResponseDTO.getPageNumber()))
                .andExpect(jsonPath("$.totalPages").value(roomsResponseDTO.getTotalPages()))
                .andExpect(jsonPath("$.roomDtoList.length()").value(1))
                .andExpect(jsonPath("$.roomDtoList[0].id").value(room.getId()))
                .andExpect(jsonPath("$.roomDtoList[0].name").value(room.getName()))
                .andExpect(jsonPath("$.roomDtoList[0].type").value(room.getType()))
                .andExpect(jsonPath("$.roomDtoList[0].price").value(room.getPrice()))
                .andExpect(jsonPath("$.roomDtoList[0].available").value(room.isAvailable()));

        verify(roomService).getAvailableRooms(pageNumber);
    }

    @Test
    @DisplayName("Get available rooms returns empty list when no rooms are available")
    void getAvailableRooms_ReturnsEmptyList_WhenNoRoomsAreAvailable() throws Exception {
        int pageNumber = 0;
        RoomsResponseDTO roomsResponseDTO = new RoomsResponseDTO();
        roomsResponseDTO.setPageNumber(0);
        roomsResponseDTO.setTotalPages(0);
        roomsResponseDTO.setRoomDtoList(List.of());

        when(roomService.getAvailableRooms(pageNumber)).thenReturn(roomsResponseDTO);

        mockMvc.perform(get("/api/customer/rooms/{pageNumber}", pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.roomDtoList.length()").value(0));

        verify(roomService).getAvailableRooms(pageNumber);
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
