package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.PaginationParamException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testCreateItemRequest() {
        Long requesterId = 1L;
        ItemRequestCreationDto requestDto = ItemRequestCreationDto.builder().build();
        requestDto.setDescription("Test Description");

        User user = new User();
        user.setId(requesterId);
        when(userRepository.findById(eq(requesterId))).thenReturn(Optional.of(user));

        ItemRequest savedItemRequest = new ItemRequest();
        savedItemRequest.setId(1L);
        savedItemRequest.setDescription("Test Description");
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedItemRequest);

        ItemRequestCreationDto result = itemRequestService.create(requestDto, requesterId);

        assertNotNull(result);
        assertEquals("Test Description", result.getDescription());

        verify(userRepository).findById(eq(requesterId));

        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    public void testCreateItemRequestWithNonExistentUser() {
        Long requesterId = 1L;
        ItemRequestCreationDto requestDto = ItemRequestCreationDto.builder().build();

        when(userRepository.findById(eq(requesterId))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.create(requestDto, requesterId));

        verify(userRepository).findById(eq(requesterId));

        verify(itemRequestRepository, Mockito.never()).save(any(ItemRequest.class));
    }

    @Test
    public void testGetRequestsValidUser() {
        Long requesterId = 1L;
        User user = User.builder()
                .id(requesterId)
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.now())
                .description("description")
                .build();

        List<Item> items = List.of(Item.builder()
                        .id(1L)
                        .request(itemRequest)
                        .build());

        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRepository.findByRequestIdIn(any())).thenReturn(items);
        when(itemRequestRepository.findAllByRequesterId(requesterId)).thenReturn(List.of(itemRequest));

        Collection<ItemRequestSendingDto> result = itemRequestService.getRequests(requesterId);

        assertNotNull(result);

        verify(userRepository, times(1)).existsById(requesterId);
        verify(itemRequestRepository, times(1)).findAllByRequesterId(requesterId);
    }

    @Test
    public void testGetRequestsWithValidParams() {
        int from = 0;
        int size = 10;
        long userId = 1L;

        User user = User.builder()
                .id(userId)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);

        List<ItemRequest> itemRequests = Collections.emptyList(); // Replace with valid item requests
        Page<ItemRequest> pageResult = new PageImpl<>(itemRequests);
        when(itemRequestRepository.findAll(userId, PageRequest.of(0, size))).thenReturn(pageResult);

        Collection<ItemRequestSendingDto> result = itemRequestService.getRequests(from, size, userId);

        assertNotNull(result);

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findAll(userId, PageRequest.of(0, size));
    }

    @Test
    public void testGetRequestsWithInvalidUser() {
        int from = 0;
        int size = 10;
        long userId = 2L; // An invalid user
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequests(from, size, userId));
    }

    @Test
    public void testGetRequestsWithInvalidParams() {
        int from = -1; // Invalid 'from' parameter
        int size = 0; // Invalid 'size' parameter
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(PaginationParamException.class, () -> itemRequestService.getRequests(from, size, userId));
    }

    @Test
    public void testGetRequestWithValidUserAndRequest() {
        long userId = 1L;
        long requestId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        ItemRequest itemRequest = new ItemRequest(); // Create a valid item request
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        List<Item> items = List.of(Item.builder()
                .id(1L)
                .request(itemRequest)
                .build());

        when(itemRepository.findByRequestId(requestId)).thenReturn(items);

        ItemRequestSendingDto result = itemRequestService.getRequest(requestId, userId);

        assertNotNull(result);

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findByRequestId(requestId);
    }

    @Test
    public void testGetRequestWithInvalidUser() {
        long userId = 2L; // An invalid user
        long requestId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);


        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequest(requestId, userId));
    }

    @Test
    public void testGetRequestWithInvalidRequest() {
        long userId = 1L;
        long requestId = 2L;
        when(userRepository.existsById(userId)).thenReturn(true);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> itemRequestService.getRequest(requestId, userId));
    }
}
