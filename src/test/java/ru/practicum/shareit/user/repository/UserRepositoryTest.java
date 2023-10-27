package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail_UserExists() {
        String email = "john@example.com";
        User user = User.builder()
                .name("JohnDoe")
                .email(email)
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

    @Test
    public void testFindByEmail_UserDoesNotExist() {
        String email = "jane@example.com";

        Optional<User> foundUser = userRepository.findByEmail(email);

        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testExistsByEmail_EmailExists() {
        String email = "john@example.com";
        User user = User.builder()
                .name("JohnDoe")
                .email(email)
                .build();
        userRepository.save(user);

        boolean existsByEmail = userRepository.existsByEmail(email);

        assertTrue(existsByEmail);
    }

    @Test
    public void testExistsByEmail_EmailDoesNotExist() {
        String email = "jane@example.com";

        boolean existsByEmail = userRepository.existsByEmail(email);

        assertFalse(existsByEmail);
    }
}
