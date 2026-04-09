package com.pitercoding.backend.controller.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pitercoding.backend.configs.JwtAuthenticationFilter;
import com.pitercoding.backend.dto.ReservationDTO;
import com.pitercoding.backend.dto.ReservationResponseDTO;
import com.pitercoding.backend.enums.ReservationStatus;
import com.pitercoding.backend.services.customer.booking.BookingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Post booking returns ok when reservation is created successfully")
    void postBooking_ReturnsOk_WhenReservationIsCreatedSuccessfully() throws Exception {
        ReservationDTO reservationDTO = createReservationDTO();

        when(bookingService.postReservation(any(ReservationDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/customer/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isOk());

        verify(bookingService).postReservation(any(ReservationDTO.class));
    }

    @Test
    @DisplayName("Post booking returns not found when reservation is not created")
    void postBooking_ReturnsNotFound_WhenReservationIsNotCreated() throws Exception {
        ReservationDTO reservationDTO = createReservationDTO();

        when(bookingService.postReservation(any(ReservationDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/customer/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isNotFound());

        verify(bookingService).postReservation(any(ReservationDTO.class));
    }

    @Test
    @DisplayName("Get all bookings by user id returns reservations when request is successful")
    void getAllBookingsByUserId_ReturnsReservations_WhenRequestIsSuccessful() throws Exception {
        Long userId = 1L;
        int pageNumber = 0;
        ReservationResponseDTO reservationResponseDTO = createReservationResponseDTO();

        when(bookingService.getAllReservationsByUserId(userId, pageNumber)).thenReturn(reservationResponseDTO);

        mockMvc.perform(get("/api/customer/bookings/{userId}/{pageNumber}", userId, pageNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(reservationResponseDTO.getPageNumber()))
                .andExpect(jsonPath("$.totalPages").value(reservationResponseDTO.getTotalPages()))
                .andExpect(jsonPath("$.reservationDtoList[0].id").value(reservationResponseDTO.getReservationDtoList().get(0).getId()))
                .andExpect(jsonPath("$.reservationDtoList[0].userId").value(reservationResponseDTO.getReservationDtoList().get(0).getUserId()))
                .andExpect(jsonPath("$.reservationDtoList[0].roomId").value(reservationResponseDTO.getReservationDtoList().get(0).getRoomId()))
                .andExpect(jsonPath("$.reservationDtoList[0].roomName").value(reservationResponseDTO.getReservationDtoList().get(0).getRoomName()))
                .andExpect(jsonPath("$.reservationDtoList[0].roomType").value(reservationResponseDTO.getReservationDtoList().get(0).getRoomType()))
                .andExpect(jsonPath("$.reservationDtoList[0].price").value(reservationResponseDTO.getReservationDtoList().get(0).getPrice()))
                .andExpect(jsonPath("$.reservationDtoList[0].reservationStatus")
                        .value(reservationResponseDTO.getReservationDtoList().get(0).getReservationStatus().name()));

        verify(bookingService).getAllReservationsByUserId(userId, pageNumber);
    }

    @Test
    @DisplayName("Get all bookings by user id returns internal server error when service throws exception")
    void getAllBookingsByUserId_ReturnsInternalServerError_WhenServiceThrowsException() throws Exception {
        Long userId = 1L;
        int pageNumber = 0;

        when(bookingService.getAllReservationsByUserId(userId, pageNumber))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/customer/bookings/{userId}/{pageNumber}", userId, pageNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong."));

        verify(bookingService).getAllReservationsByUserId(userId, pageNumber);
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
