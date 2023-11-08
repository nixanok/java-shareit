package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRepositoryTest {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private Item testItem1;

    private Item testItem2;

    private User testOwner1;

    private User testOwner2;

    @BeforeEach
    public void setup() {

        testOwner1 = User.builder()
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        testOwner2 = User.builder()
                .name("RobertSimpson")
                .email("simpson@example.com")
                .build();

        testItem1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(testOwner1)
                .build();
        testItem2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .owner(testOwner2)
                .build();

        userRepository.save(testOwner1);
        userRepository.save(testOwner2);
    }

    @AfterEach
    public void clear() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void testFindAllByNameAndDescription() {

        itemRepository.saveAll(Arrays.asList(testItem1, testItem2));

        List<Item> foundItems = itemRepository.findAllByNameAndDescription("item");

        assertEquals(2, foundItems.size());
        assertEquals("Item 1", foundItems.get(0).getName());
        assertEquals("Item 2", foundItems.get(1).getName());
    }


    @Test
    public void testFindAllByOwnerId() {

        testOwner2.setEmail("testEmail@email.com");

        testOwner2 = userRepository.save(testOwner2);

        final long ownerId = testOwner2.getId();

        testItem1.setOwner(testOwner2);
        testItem2.setOwner(testOwner2);

        itemRepository.saveAll(Arrays.asList(testItem1, testItem2));

        List<Item> foundItems = itemRepository.findAllByOwnerId(ownerId);

        assertEquals(2, foundItems.size());
        assertEquals("Item 1", foundItems.get(0).getName());
        assertEquals("Item 2", foundItems.get(1).getName());
        assertEquals(ownerId, foundItems.get(0).getOwner().getId());
        assertEquals(ownerId, foundItems.get(1).getOwner().getId());
    }

}
