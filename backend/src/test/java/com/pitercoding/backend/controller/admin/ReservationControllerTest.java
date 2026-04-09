package com.pitercoding.backend.controller.admin;

import com.pitercoding.backend.configs.JwtAuthenticationFilter;
import com.pitercoding.backend.dto.ReservationDTO;
import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.services.admin.reservation.ReservationService;
import com.pitercoding.backend.services.jwt.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Get all reservations returns reservations when request is successful")
    void getAllReservations_ReturnsReservations_WhenRequestIsSuccessful() throws Exception {
        int pageNumber = 0;
        ReservationResponseDTO reservationResponseDTO = createReservationResponseDTO();

        when(reservationService.getAllReservations(pageNumber)).thenReturn(reservationResponseDTO);

        mockMvc.perform(get("/api/admin/reservations/{pageNumber}", pageNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(reservationResponseDTO.getPageNumber()))
                .andExpect(jsonPath("$.totalPages").value(reservationResponseDTO.getTotalPages()))
                .andExpect(jsonPath("$.reservationDtoList[0].id")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getId()))
                .andExpect(jsonPath("$.reservationDtoList[0].userId")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getUserId()))
                .andExpect(jsonPath("$.reservationDtoList[0].roomId")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getRoomId()))
                .andExpect(jsonPath("$.reservationDtoList[0].roomName")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getRoomName()))
                .andExpect(jsonPath("$.reservationDtoList[0].roomType")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getRoomType()))
                .andExpect(jsonPath("$.reservationDtoList[0].price")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getPrice()))
                .andExpect(jsonPath("$.reservationDtoList[0].reservationStatus")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getReservationStatus().name()));

        verify(reservationService).getAllReservations(pageNumber);
    }

    @Test
    @DisplayName("Get all reservations returns internal server error when service throws exception")
    void getAllReservations_ReturnsInternalServerError_WhenServiceThrowsException() throws Exception {
        int pageNumber = 0;

        when(reservationService.getAllReservations(pageNumber))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/admin/reservations/{pageNumber}", pageNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong."));

        verify(reservationService).getAllReservations(pageNumber);
    }

    @Test
    @DisplayName("Change reservation status returns ok when update is successful")
    void changeReservationStatus_ReturnsOk_WhenUpdateIsSuccessful() throws Exception {
        Long reservationId = 1L;
        String reservationStatus = ReservationStatus.APPROVED.name();

        when(reservationService.changeReservationStatus(reservationId, reservationStatus)).thenReturn(true);

        mockMvc.perform(put("/api/admin/reservation/{reservationId}/{reservationStatus}",
                        reservationId, reservationStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reservationService).changeReservationStatus(reservationId, reservationStatus);
    }

    @Test
    @DisplayName("Change reservation status returns internal server error when update fails")
    void changeReservationStatus_ReturnsInternalServerError_WhenUpdateFails() throws Exception {
        Long reservationId = 1L;
        String reservationStatus = ReservationStatus.APPROVED.name();

        when(reservationService.changeReservationStatus(reservationId, reservationStatus))
                .thenReturn(false);

        mockMvc.perform(put("/api/admin/reservation/{reservationId}/{reservationStatus}",
                        reservationId, reservationStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong."));

        verify(reservationService).changeReservationStatus(reservationId, reservationStatus);
    }

    private ReservationDTO createReservationDTO() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(1L);
        reservationDTO.setUserId(1L);
        reservationDTO.setUserName("Racha Cuca");
        reservationDTO.setRoomId(1L);
        reservationDTO.setRoomName("Room 1");
        reservationDTO.setRoomType("Studio");
        reservationDTO.setCheckInDate(LocalDate.of(2026, 4, 9));
        reservationDTO.setCheckOutDate(LocalDate.of(2026, 4, 11));
        reservationDTO.setPrice(1000L);
        reservationDTO.setReservationStatus(ReservationStatus.PENDING);
        return reservationDTO;
    }

    private ReservationResponseDTO createReservationResponseDTO() {
        ReservationResponseDTO responseDTO = new ReservationResponseDTO();
        responseDTO.setPageNumber(0);
        responseDTO.setTotalPages(1);
        responseDTO.setReservationDtoList(List.of(createReservationDTO()));
        return responseDTO;
    }
}
