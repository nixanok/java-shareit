package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingApproveDto;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.ErrorController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ErrorController())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateBooking_Successful() throws Exception {

        Long ownerId = 1L;
        BookingCreationDto booking = BookingCreationDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        BookingSendingDto bookingSendingDto = BookingSendingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDto.builder()
                        .id(booking.getId())
                        .build())
                .build();

        when(bookingService.create(booking, ownerId)).thenReturn(bookingSendingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(booking.getId()));

        verify(bookingService, times(1)).create(booking, ownerId);
    }

    @Test
    void testApproveOrRejectBooking_Successful() throws Exception {
        long bookingId = 1L;
        boolean isApproved = true;
        long ownerId = 1L;

        Booking expectedBookingApproveDto = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(Item.builder().id(1L).build())
                .status(Status.APPROVED)
                .build();

        BookingApproveDto approveDto = BookingApproveDto.builder()
                .id(bookingId)
                .start(expectedBookingApproveDto.getStart())
                .end(expectedBookingApproveDto.getEnd())
                .item(ItemDto.builder().id(1L).build())
                .build();

        when(bookingService.approve(bookingId, isApproved, ownerId)).thenReturn(expectedBookingApproveDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(isApproved))
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(Status.APPROVED.toString()))
                .andExpect(jsonPath("$.item.id").value(1L));
    }

    @Test
    void testGetBooking_Successful() throws Exception {

        long bookingId = 1L;
        long userId = 1L;

        BookingSendingDto expectedBookingSendingDto = BookingSendingDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(ItemDto.builder()
                        .id(1L)
                        .build())
                .build();

        LocalDateTime expectedStart = expectedBookingSendingDto.getStart();
        LocalDateTime expectedEnd = expectedBookingSendingDto.getEnd();


        when(bookingService.get(bookingId, userId)).thenReturn(expectedBookingSendingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.start[0]").value(expectedStart.getYear()))
                .andExpect(jsonPath("$.end[0]").value(expectedEnd.getYear()))
                .andExpect(jsonPath("$.item.id").value(expectedBookingSendingDto.getItem().getId()));

        verify(bookingService, times(1)).get(bookingId, userId);
    }

    @Test
    public void testGetBookingsByBookerId() throws Exception {
        long bookerId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        when(bookingService.getBookingsByBookerId(bookerId, state, from, size))
                .thenReturn(List.of(BookingSendingDto.builder()
                                .id(1L)
                                .start(LocalDateTime.now())
                                .end(LocalDateTime.now())
                                .item(ItemDto.builder()
                                        .id(1L)
                                        .build())
                                .build()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                        .param("state", state.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].item.id").value(1L));

        verify(bookingService, times(1)).getBookingsByBookerId(bookerId, state, from, size);
    }

    @Test
    public void testGetBookingsItemsByUserId() throws Exception {
        long ownerId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        when(bookingService.getBookingsItemsByUserId(ownerId, state, from, size))
                .thenReturn(List.of(BookingSendingDto.builder()
                        .id(1L)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now())
                        .item(ItemDto.builder()
                                .id(1L)
                                .build())
                        .build()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                        .param("state", state.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].item.id").value(1L));

        verify(bookingService, times(1)).getBookingsItemsByUserId(ownerId, state, from, size);
    }
}
