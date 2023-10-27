package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.booking.exception.BookingTimeException;
import ru.practicum.shareit.booking.exception.OwnerNotFoundException;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.tool.ItemMapper;
import ru.practicum.shareit.request.exception.PaginationParamException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.tool.UserMapper;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    public void setup() {
        booking1 = new Booking();
        booking1.setId(1L);

        booking2 = new Booking();
        booking2.setId(2L);

        booking3 = new Booking();
        booking3.setId(3L);
    }

    @Test
    void create_ShouldCreateBooking_Successful() {

        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .end(LocalDateTime.of(2022, 1, 1, 13, 0))
                .itemId(1L)
                .build();

        User owner = User.builder()
                .id(1L)
                .build();

        User broker = User.builder()
                .id(2L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Booking expectedBooking = Booking.builder()
                .id(1L)
                .booker(broker)
                .item(item)
                .status(Status.WAITING)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userRepository.findById(2L)).thenReturn(Optional.of(broker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        BookingSendingDto result = bookingService.create(bookingCreationDto, broker.getId());

        assertEquals(1L, result.getId());
        assertEquals(UserMapper.toDto(broker), result.getBooker());
        assertEquals(ItemMapper.toDto(item), result.getItem());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void create_ShouldThrowBookingTimeException_WhenStartIsNotBeforeEnd() {
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .end(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();

        assertThrows(BookingTimeException.class, () -> bookingService.create(bookingCreationDto, 1L));
    }

    @Test
    void create_ShouldThrowUserNotFoundException_WhenBrokerIdIsInvalid() {
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .end(LocalDateTime.of(2022, 1, 1, 13, 0))
                .build();
        bookingCreationDto.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.create(bookingCreationDto, 1L));
    }

    @Test
    void create_ShouldThrowItemNotFoundException_WhenItemIdIsInvalid() {
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .end(LocalDateTime.of(2022, 1, 1, 13, 0))
                .build();
        bookingCreationDto.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.create(bookingCreationDto, 1L));
    }


    @Test
    void approve_ShouldThrowBookingNotFoundException_WhenBookingIdIsInvalid() {
        Long bookingId = 1L;
        Boolean isApproved = true;
        Long ownerId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.approve(bookingId, isApproved, ownerId));
    }

    @Test
    void approve_ShouldThrowBookingStatusException_WhenBookingIsAlreadyApproved() {
        Long bookingId = 1L;
        Boolean isApproved = true;
        Long ownerId = 2L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(Status.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingStatusException.class, () -> bookingService.approve(bookingId, isApproved, ownerId));
    }

    @Test
    void approve_ShouldThrowOwnerNotFoundException_WhenOwnerIdIsInvalid() {
        Long bookingId = 1L;
        Boolean isApproved = true;
        Long ownerId = 2L;

        User owner = User.builder()
                .id(3L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setId(bookingId);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(OwnerNotFoundException.class, () -> bookingService.approve(bookingId, isApproved, ownerId));
    }

    @Test
    void approve_ShouldUpdateBookingStatusToApproved_WhenIsApprovedIsTrue() {
        Long bookingId = 1L;
        Boolean isApproved = true;
        Long ownerId = 2L;

        User owner = User.builder()
                .id(ownerId)
                .build();

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.approve(bookingId, isApproved, ownerId);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void approve_ShouldUpdateBookingStatusToRejected_WhenIsApprovedIsFalse() {
        Long bookingId = 1L;
        Boolean isApproved = false;
        Long ownerId = 2L;

        User owner = User.builder()
                .id(ownerId)
                .build();

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.approve(bookingId, isApproved, ownerId);

        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void getBookingsByBookerId_ShouldThrowUserNotFoundException_WhenBookerIdIsInvalid() {
        Long bookerId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        when(userRepository.existsById(bookerId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingsByBookerId(bookerId, state, from, size));
    }

    @Test
    public void testGetBookingsByBookerId_Successful_WhenStateIsALL() {
        Long bookerId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        User broker = new User();
        broker.setId(bookerId);

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(broker)
                .build();

        booking1.setBooker(broker);
        booking2.setBooker(broker);
        booking1.setItem(item);
        booking2.setItem(item);

        List<Booking> bookingList = List.of(booking1, booking2);

        Page<Booking> page = new PageImpl<>(bookingList, PageRequest.of(from, size), size - from);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findByBookerId(eq(bookerId), any(PageRequest.class))).thenReturn(page);

        List<BookingSendingDto> result = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsByBookerId_Successful_WhenStateIsWaiting() {
        Long bookerId = 1L;
        State state = State.WAITING;
        int from = 0;
        int size = 10;

        User broker = new User();
        broker.setId(bookerId);

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(broker)
                .build();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(broker);
        booking1.setItem(item);
        booking1.setStatus(Status.WAITING);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(broker);
        booking2.setItem(item);
        booking2.setStatus(Status.WAITING);

        List<Booking> bookingList = List.of(booking1, booking2);

        Page<Booking> page = new PageImpl<>(bookingList, PageRequest.of(from, size), size - from);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatus(
                eq(bookerId), eq(Status.WAITING), any(PageRequest.class)))
                .thenReturn(page);

        List<BookingSendingDto> result = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsByBookerId_Successful_WhenStateIsPast() {
        Long bookerId = 1L;
        State state = State.PAST;
        int from = 0;
        int size = 10;

        User broker = new User();
        broker.setId(bookerId);

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(broker)
                .build();

        LocalDateTime currentTime = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(broker);
        booking1.setItem(item);
        booking1.setEnd(currentTime.minusDays(1));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(broker);
        booking2.setItem(item);
        booking2.setEnd(currentTime.minusDays(2));

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndIsBefore(
                eq(bookerId), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingSendingDto> result = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsByBookerId_Successful_WhenStateIsFuture() {
        Long bookerId = 1L;
        State state = State.FUTURE;
        int from = 0;
        int size = 10;

        User broker = new User();
        broker.setId(bookerId);

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(broker)
                .build();

        LocalDateTime currentTime = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(broker);
        booking1.setItem(item);
        booking1.setStart(currentTime.plusDays(1));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(broker);
        booking2.setItem(item);
        booking2.setStart(currentTime.plusDays(2));

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartIsAfter(
                eq(bookerId), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingSendingDto> result = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsByBookerId_Successful_WhenStateIsCurrent() {
        Long bookerId = 1L;
        State state = State.CURRENT;
        int from = 0;
        int size = 10;

        User broker = new User();
        broker.setId(bookerId);

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(broker)
                .build();

        LocalDateTime currentTime = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(broker);
        booking1.setItem(item);
        booking1.setStart(currentTime.minusHours(2));
        booking1.setEnd(currentTime.plusHours(2));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(broker);
        booking2.setItem(item);
        booking2.setStart(currentTime.minusHours(1));
        booking2.setEnd(currentTime.plusHours(1));

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(bookerId)).thenReturn(true);

        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                eq(bookerId), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingSendingDto> result = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsByBookerId_Successful_WhenStateIsRejected() {
        Long bookerId = 1L;
        State state = State.REJECTED;
        int from = 0;
        int size = 10;

        User broker = new User();
        broker.setId(bookerId);

        Item item = Item.builder()
                .id(1L)
                .available(true)
                .owner(broker)
                .build();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(broker);
        booking1.setItem(item);
        booking1.setStatus(Status.REJECTED);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBooker(broker);
        booking2.setItem(item);
        booking2.setStatus(Status.REJECTED);

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(bookerId)).thenReturn(true);

        PageImpl<Booking> page = new PageImpl<>(bookingList);
        when(bookingRepository.findByBookerIdAndStatus(
                eq(bookerId), eq(Status.REJECTED), any(PageRequest.class)))
                .thenReturn(page);

        List<BookingSendingDto> result = bookingService.getBookingsByBookerId(bookerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsByBookerId_UserNotFound() {
        Long bookerId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        when(userRepository.existsById(bookerId)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingsByBookerId(bookerId, state, from, size));
    }

    @Test
    public void testGetBookingsByBookerId_InvalidPaginationParams() {
        Long bookerId = 1L;
        State state = State.ALL;
        int from = -1;
        int size = 0;

        when(userRepository.existsById(bookerId)).thenReturn(true);

        assertThrows(PaginationParamException.class, () -> bookingService.getBookingsByBookerId(bookerId, state, from, size));
    }

    @Test
    public void testGetBookingsItemsByUserId_All() {
        Long ownerId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        User owner = new User();
        owner.setId(ownerId);

        Item item1 = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .available(true)
                .owner(owner)
                .build();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(owner);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setBooker(owner);

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerId(eq(ownerId), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingSendingDto> result = bookingService.getBookingsItemsByUserId(ownerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsItemsByUserId_Past() {
        Long ownerId = 1L;
        State state = State.PAST;
        int from = 0;
        int size = 10;

        User owner = new User();
        owner.setId(ownerId);

        Item item1 = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .available(true)
                .owner(owner)
                .build();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(owner);
        booking1.setEnd(LocalDateTime.now().minusDays(2));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setBooker(owner);
        booking2.setEnd(LocalDateTime.now().minusDays(1));

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(eq(ownerId), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingSendingDto> result = bookingService.getBookingsItemsByUserId(ownerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsItemsByUserId_Future() {
        Long ownerId = 1L;
        State state = State.FUTURE;
        int from = 0;
        int size = 10;

        User owner = new User();
        owner.setId(ownerId);

        Item item1 = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .available(true)
                .owner(owner)
                .build();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(owner);
        booking1.setStart(LocalDateTime.now().plusDays(2));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setBooker(owner);
        booking2.setStart(LocalDateTime.now().plusDays(1));

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfter(
                eq(ownerId), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingSendingDto> result = bookingService.getBookingsItemsByUserId(ownerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBookingsItemsByUserId_Current() {
        Long ownerId = 1L;
        State state = State.CURRENT;
        int from = 0;
        int size = 10;

        User owner = new User();
        owner.setId(ownerId);

        Item item1 = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .available(true)
                .owner(owner)
                .build();

        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(owner);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.plusDays(1));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item2);
        booking2.setBooker(owner);
        booking2.setStart(now.minusHours(1));
        booking2.setEnd(now.plusHours(1));

        List<Booking> bookingList = List.of(booking1, booking2);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingSendingDto> result = bookingService.getBookingsItemsByUserId(ownerId, state, from, size);

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
        assertEquals(bookingList.get(0).getId(), result.get(0).getId());
        assertEquals(bookingList.get(1).getId(), result.get(1).getId());
    }

    @Test
    public void testGetBooking_ExistsAndAuthorized() {
        Long bookingId = 1L;
        Long userId = 2L;

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setOwner(booker);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingSendingDto result = bookingService.get(bookingId, userId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
    }

    @Test
    public void testGetBooking_ExistsButUnauthorized() {
        Long bookingId = 1L;
        Long userId = 2L;

        User booker = new User();
        booker.setId(3L);

        Item item = new Item();
        item.setOwner(booker);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingNotFoundException.class, () -> bookingService.get(bookingId, userId));
    }

    @Test
    public void testGetBooking_NotFound() {
        Long bookingId = 1L;
        Long userId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.get(bookingId, userId));
    }

    @Test
    public void testGetAllBookings() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();

        Item item = new Item();
        booking1.setItem(item);
        booking2.setItem(item);

        List<Booking> bookingList = List.of(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(bookingList);

        List<BookingCreationDto> result = bookingService.getAll();

        assertNotNull(result);
        assertEquals(bookingList.size(), result.size());
    }

    @Test
    public void testGetBookingById_Exists() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        Item item = new Item();
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingCreationDto result = bookingService.getById(bookingId);

        assertNotNull(result);
    }

    @Test
    public void testGetBookingById_NotFound() {
        Long nonExistentBookingId = 2L;
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(nonExistentBookingId));
    }

    @Test
    public void testRemoveBooking_Exists() {
        Long bookingId = 1L;
        when(bookingRepository.existsById(bookingId)).thenReturn(true);

        bookingService.removeById(bookingId);
        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(bookingId));
    }

    @Test
    public void testRemoveBooking_NotFound() {
        Long nonExistentBookingId = 2L;
        when(bookingRepository.existsById(nonExistentBookingId)).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () -> bookingService.removeById(nonExistentBookingId));
    }

}
