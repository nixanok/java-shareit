package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
}

