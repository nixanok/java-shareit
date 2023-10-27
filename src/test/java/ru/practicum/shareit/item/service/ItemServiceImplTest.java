package ru.practicum.shareit.item.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.tool.ItemMapper;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void testCreate_Successful() {
        long ownerId = 1L;
        long requestId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .requestId(requestId)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.fromDto(itemDto));

        ItemDto createdItem = itemService.create(itemDto, ownerId);

        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertEquals(itemDto.getAvailable(), createdItem.getAvailable());
    }

    @Test
    public void testCreate_UserNotFound() {
        long ownerId = 1L;
        long requestId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .requestId(requestId)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.create(itemDto, ownerId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void testCreate_RequestNotFound() {
        long ownerId = 1L;
        long requestId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .requestId(requestId)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> itemService.create(itemDto, ownerId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void testCreate_RequestIdNull() {
        long ownerId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.fromDto(itemDto));

        ItemDto createdItem = itemService.create(itemDto, ownerId);

        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertEquals(itemDto.getAvailable(), createdItem.getAvailable());
        assertNull(createdItem.getRequestId());
    }

    @Test
    public void testPatch_Successful() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto patchItemDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        Item existingItem = Item.builder()
                .id(itemId)
                .name("Original Name")
                .description("Original Description")
                .available(false)
                .owner(owner)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        ItemDto patchedItem = itemService.patch(itemId, patchItemDto, ownerId);

        assertEquals(patchItemDto.getName(), patchedItem.getName());
        assertEquals(patchItemDto.getDescription(), patchedItem.getDescription());
        assertEquals(patchItemDto.getAvailable(), patchedItem.getAvailable());
    }

    @Test
    public void testPatch_ItemNotFound() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto patchItemDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(true)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.patch(itemId, patchItemDto, ownerId));
    }

    @Test
    public void testPatch_OwnerMismatch() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto patchItemDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        Item existingItem = Item.builder()
                .id(itemId)
                .name("Original Name")
                .description("Original Description")
                .available(false)
                .owner(owner)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(ItemNotFoundException.class, () -> itemService.patch(itemId, patchItemDto, 2L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void testGetAll_Successful() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<ItemDto> items = itemService.getAll();

        assertEquals(2, items.size());
        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item1.getDescription(), items.get(0).getDescription());
        assertEquals(item1.getAvailable(), items.get(0).getAvailable());
        assertEquals(item2.getName(), items.get(1).getName());
        assertEquals(item2.getDescription(), items.get(1).getDescription());
        assertEquals(item2.getAvailable(), items.get(1).getAvailable());
    }

    @Test
    public void testGetAll_EmptyList() {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        List<ItemDto> items = itemService.getAll();

        assertEquals(0, items.size());
    }

    @Test
    public void testSearch_Successful() {
        String searchText = "Item";

        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        when(itemRepository.findAllByNameAndDescription(searchText))
                .thenReturn(Arrays.asList(item1, item2));

        List<ItemDto> items = itemService.search(searchText);

        assertEquals(2, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item1.getDescription(), items.get(0).getDescription());
        assertEquals(item1.getAvailable(), items.get(0).getAvailable());
        assertEquals(item2.getId(), items.get(1).getId());
        assertEquals(item2.getName(), items.get(1).getName());
        assertEquals(item2.getDescription(), items.get(1).getDescription());
        assertEquals(item2.getAvailable(), items.get(1).getAvailable());
    }

    @Test
    public void testSearch_EmptyText() {
        String searchText = "";

        List<ItemDto> items = itemService.search(searchText);

        assertTrue(items.isEmpty());
    }

    @Test
    public void testGetByOwnerId_Successful() {
        Long ownerId = 1L;

        User booker = User.builder()
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        Booking booking1 = Booking.builder()
                .id(1L)
                .item(item1)
                .booker(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(5))
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(15))
                .build();

        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(items);
        when(bookingRepository.findLatestBookingsForItems(anyList()))
                .thenReturn(Collections.singletonList(booking1));
        when(bookingRepository.findFutureBookingsForItems(anyList()))
                .thenReturn(Collections.singletonList(booking2));

        List<ItemBookingsDto> itemBookingsDtos = itemService.getByOwnerId(ownerId);

        assertEquals(2, itemBookingsDtos.size());
        assertEquals(item1.getId(), itemBookingsDtos.get(0).getId());
        assertEquals(item1.getName(), itemBookingsDtos.get(0).getName());
        assertEquals(item1.getDescription(), itemBookingsDtos.get(0).getDescription());
        assertEquals(item1.getAvailable(), itemBookingsDtos.get(0).getAvailable());
        assertEquals(booking1.getId(), itemBookingsDtos.get(0).getLastBooking().getId());
        assertNull(itemBookingsDtos.get(0).getNextBooking());
        assertEquals(item2.getId(), itemBookingsDtos.get(1).getId());
        assertEquals(item2.getName(), itemBookingsDtos.get(1).getName());
        assertEquals(item2.getDescription(), itemBookingsDtos.get(1).getDescription());
        assertEquals(item2.getAvailable(), itemBookingsDtos.get(1).getAvailable());
        assertNull(itemBookingsDtos.get(1).getLastBooking());
        assertEquals(booking2.getId(), itemBookingsDtos.get(1).getNextBooking().getId());
    }

    @Test
    public void testGetByOwnerId_NoItemsFound() {
        Long ownerId = 1L;

        when(itemRepository.findAllByOwnerId(ownerId)).thenReturn(Collections.emptyList());

        List<ItemBookingsDto> itemBookingsDtos = itemService.getByOwnerId(ownerId);

        assertTrue(itemBookingsDtos.isEmpty());
    }

    @Test
    public void testRemoveById_Successful() {
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);
        itemService.removeById(itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void testRemoveById_NotFound() {
        Long itemId = 1L;

        doThrow(EmptyResultDataAccessException.class)
                .when(itemRepository)
                .deleteById(itemId);

        assertThrows(ItemNotFoundException.class, () -> itemService.removeById(itemId));
    }

    @Test
    public void testGetItemBookings_ExistsAndOwner() {
        Long itemId = 1L;
        Long ownerId = 2L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking approvedBooking1 = new Booking();
        approvedBooking1.setStart(LocalDateTime.now().plusDays(1));
        approvedBooking1.setEnd(LocalDateTime.now().plusDays(1));
        approvedBooking1.setStatus(Status.APPROVED);

        List<Comment> comments = List.of(new Comment(), new Comment());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatus(
                eq(itemId), any(LocalDateTime.class), eq(Status.APPROVED), any(Sort.class)))
                .thenReturn(approvedBooking1);

        ItemBookingsDto result = itemService.getById(itemId, ownerId);
        when(commentRepository.findAllByItemIdOrderByCreated(itemId)).thenReturn(comments);
        assertNotNull(result);
    }

    @Test
    public void testGetItemBookings_NotFound() {
        Long nonExistentItemId = 2L;
        Long ownerId = 1L;

        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getById(nonExistentItemId, ownerId));
    }
}
