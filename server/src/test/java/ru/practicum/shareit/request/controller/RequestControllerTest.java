package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateRequest() throws Exception {
        Long requesterId = 1L;
        ItemRequestCreationDto requestDto = ItemRequestCreationDto.builder()
                .description("Test Description")
                .requesterId(requesterId)
                .build();

        when(itemRequestService.create(any(ItemRequestCreationDto.class), eq(requesterId))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requesterId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requesterId").value(requesterId.intValue()));

        verify(itemRequestService, times(1)).create(any(ItemRequestCreationDto.class), eq(requesterId));
    }

    @Test
    public void testCreateRequestWithoutHeader() throws Exception {
        ItemRequestCreationDto requestDto = ItemRequestCreationDto.builder()
                .description("Test Description")
                .requesterId(1L)
                .build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(any(ItemRequestCreationDto.class), any(Long.class));
    }

    @Test
    public void testCreateRequestWithEmptyDescription() throws Exception {
        Long requesterId = 1L;
        ItemRequestCreationDto requestDto = ItemRequestCreationDto.builder()
                .description("")
                .requesterId(requesterId)
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requesterId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(any(ItemRequestCreationDto.class), any(Long.class));
    }

    @Test
    public void testGetRequestsByRequester() throws Exception {
        Long requesterId = 1L;
        Collection<ItemRequestSendingDto> requestDtos = Arrays.asList(
                ItemRequestSendingDto.builder()
                        .description("Request 1")
                        .build(),
                ItemRequestSendingDto.builder()
                        .description("Request 2")
                        .build()
        );

        when(itemRequestService.getRequests(requesterId)).thenReturn(requestDtos);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requesterId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].description").value("Request 2"));

        verify(itemRequestService, times(1)).getRequests(requesterId);
    }

    @Test
    public void testGetRequestsByRequesterWithoutHeader() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequests(any(Long.class));
    }

    @Test
    public void testGetRequests() throws Exception {
        int from = 0;
        int size = 10;
        long userId = 1L;

        Collection<ItemRequestSendingDto> requestDtos = Arrays.asList(
                ItemRequestSendingDto.builder()
                        .description("Request 1")
                        .build(),
                ItemRequestSendingDto.builder()
                        .description("Request 2")
                        .build()
        );

        when(itemRequestService.getRequests(from, size, userId)).thenReturn(requestDtos);

        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Request 1"))
                .andExpect(jsonPath("$[1].description").value("Request 2"));

        verify(itemRequestService, times(1)).getRequests(from, size, userId);
    }

    @Test
    public void testGetRequestsWithoutHeader() throws Exception {
        int from = 0;
        int size = 10;

        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequests(anyInt(), anyInt(), anyLong());
    }

    @Test
    public void testGetRequest() throws Exception {
        Long requestId = 1L;
        long userId = 1L;

        ItemRequestSendingDto requestDto = ItemRequestSendingDto.builder()
                .description("Request 1")
                .build();

        when(itemRequestService.getRequest(requestId, userId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Request 1"));

        verify(itemRequestService, times(1)).getRequest(requestId, userId);
    }

    @Test
    public void testGetRequestWithoutHeader() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequest(anyLong(), anyLong());
    }
}
