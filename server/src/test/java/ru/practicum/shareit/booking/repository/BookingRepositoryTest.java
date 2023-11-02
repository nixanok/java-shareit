package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private Booking testBooking1;
    private Booking testBooking2;

    @BeforeEach
    public void setup() {

        user1 = User.builder()
                .name("JohnDoe")
                .email("example@example.com")
                .build();

        user2 = User.builder()
                .name("JimCarter")
                .email("jim@example.com")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(user1)
                .build();
        item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .owner(user2)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    public void testFindByBookerId() {
        Pageable pageable = PageRequest.of(0, 5);

        testBooking1 = Booking.builder()
                .booker(user1)
                .item(item2)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(Status.APPROVED)
                .build();

        bookingRepository.save(testBooking1);

        var bookings = bookingRepository.findByBookerId(user1.getId(), pageable);

        assertNotNull(bookings);
        assertEquals(1, bookings.getNumberOfElements());
    }

    @Test
    void findLatestBookingsForItems_ShouldReturnLatestApprovedBookingsForGivenItemIds() {
        Booking booking1 = Booking.builder()
                .booker(user1)
                .item(item1)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now())
                .status(Status.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user2)
                .item(item1)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(1).plusHours(1))
                .status(Status.APPROVED)
                .build();

        Booking booking3 = Booking.builder()
                .booker(user1)
                .item(item2)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(Status.WAITING)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

        List<Booking> latestBookings = bookingRepository.findLatestBookingsForItems(
                Arrays.asList(item1.getId(), item2.getId()));

        assertEquals(1, latestBookings.size());
        assertTrue(latestBookings.get(0).getItem().getId()
                .equals(item1.getId()) || latestBookings.get(0).getItem().getId().equals(item2.getId()));
        assertEquals(Status.APPROVED, latestBookings.get(0).getStatus());
    }

    @Test
    void findFutureBookingsForItems_ShouldReturnFutureApprovedBookingsForGivenItemIds() {
        Booking booking1 = Booking.builder()
                .booker(user1)
                .item(item1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(Status.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user2)
                .item(item1)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(1).plusHours(1))
                .status(Status.WAITING)
                .build();

        Booking booking3 = Booking.builder()
                .booker(user1)
                .item(item2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

        List<Booking> futureBookings = bookingRepository.findFutureBookingsForItems(Arrays.asList(item1.getId(), item2.getId()));

        assertEquals(1, futureBookings.size());
        assertTrue(futureBookings.get(0).getItem().getId().equals(item1.getId()) || futureBookings.get(0).getItem().getId().equals(item2.getId()));
        assertEquals(Status.APPROVED, futureBookings.get(0).getStatus());
    }

}

