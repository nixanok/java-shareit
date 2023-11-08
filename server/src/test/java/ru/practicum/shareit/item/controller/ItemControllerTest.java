package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.error.ErrorController;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ItemController itemController;

    private ItemDto itemDto;

    private List<ItemBookingsDto> itemBookingsDtoList;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(new ErrorController())
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item")
                .description("Test description")
                .available(true)
                .build();

        itemBookingsDtoList = new ArrayList<>();

        BookingItemDto lastBooking = BookingItemDto.builder()
                .id(1L)
                .bookerId(10L)
                .build();

        BookingItemDto nextBooking = BookingItemDto.builder()
                .id(2L)
                .bookerId(20L)
                .build();

        List<CommentDto> comments = new ArrayList<>();

        ItemBookingsDto itemBookingsDto1 = ItemBookingsDto.builder()
                .id(1L)
                .name("First Item")
                .description("First Item Description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        ItemBookingsDto itemBookingsDto2 = ItemBookingsDto.builder()
                .id(2L)
                .name("Second Item")
                .description("Second Item Description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        itemBookingsDtoList.add(itemBookingsDto1);
        itemBookingsDtoList.add(itemBookingsDto2);
    }

    @Test
    void testCreateItem_Successful() throws Exception {
        Long ownerId = 1L;

        when(itemService.create(itemDto, ownerId)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1)).create(itemDto, ownerId);
    }

    @Test
    void testPatchItem_Successfully() {
        long itemId = 1L;
        long ownerId = 1L;

        ItemDto updatedDto = ItemDto.builder()
                .id(itemId)
                .name("Updated item")
                .description("Updated description")
                .available(false)
                .build();

        when(itemService.patch(eq(itemId), any(ItemDto.class), eq(ownerId))).thenReturn(updatedDto);

        ItemDto result = itemController.patchItem(itemId, itemDto, ownerId);

        verify(itemService, times(1)).patch(eq(itemId), any(ItemDto.class), eq(ownerId));
        Assertions.assertEquals(updatedDto, result);
    }

    @Test
    void testPatchItem_NotFound() {
        long itemId = 1L;
        long ownerId = 1L;
        when(itemService.patch(eq(itemId), any(ItemDto.class), eq(ownerId))).thenThrow(new ItemNotFoundException(1L));
        assertThrows(ItemNotFoundException.class, () -> itemController.patchItem(itemId, itemDto, ownerId));
    }

    @Test
    void testGetItemsByOwnerId_Successfully() {
        long ownerId = 1L;

        when(itemService.getByOwnerId(ownerId)).thenReturn(itemBookingsDtoList);

        List<ItemBookingsDto> result = itemController.getItemsByOwnerId(ownerId);

        verify(itemService, times(1)).getByOwnerId(ownerId);
        assertEquals(itemBookingsDtoList, result);
    }

    @Test
    void testGetItemsByOwnerId_NotFound() {
        long ownerId = 1L;
        when(itemService.getByOwnerId(ownerId)).thenThrow(new ItemNotFoundException(1L));
        assertThrows(ItemNotFoundException.class, () -> itemController.getItemsByOwnerId(ownerId));
    }

    @Test
    void testSearchItems_Successfully() throws Exception {
        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("First Item")
                .description("First Item Description")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("Second Item")
                .description("Second Item Description")
                .available(true)
                .build();

        List<ItemDto> mockItems = List.of(item1, item2);

        when(itemService.search("Item")).thenReturn(mockItems);

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("First Item")))
                .andExpect(jsonPath("$.[0].description", is("First Item Description")))
                .andExpect(jsonPath("$.[0].available", is(true)))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].name", is("Second Item")))
                .andExpect(jsonPath("$.[1].description", is("Second Item Description")))
                .andExpect(jsonPath("$.[1].available", is(true)))
                .andReturn();

        verify(itemService, times(1)).search("Item");
    }

    @Test
    public void testGetItem_Successful() throws Exception {
        Long itemId = 1L;
        long ownerId = 10L;
        ItemBookingsDto expectedItemBookingsDto = itemBookingsDtoList.get(0);

        when(itemService.getById(itemId, ownerId)).thenReturn(expectedItemBookingsDto);

        MvcResult mvcResult = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ItemBookingsDto actualItemBookingsDto = objectMapper.readValue(response, ItemBookingsDto.class);

        Assertions.assertEquals(expectedItemBookingsDto, actualItemBookingsDto);
        verify(itemService, times(1)).getById(itemId, ownerId);
    }

    @Test
    public void removeItemByIdTest() throws Exception {
        long itemId = 1L;

        doNothing().when(itemService).removeById(itemId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1)).removeById(itemId);
    }

}