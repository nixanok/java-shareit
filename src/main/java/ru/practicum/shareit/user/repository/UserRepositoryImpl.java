package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    private long nextId = 1;

    @Override
    public User add(User user) {
        if (containsEmail(user.getEmail())) {
            throw new EmailAlreadyExistException(user.getEmail());
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public boolean contains(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getById(Long id) {
        return users.containsKey(id) ? Optional.of(users.get(id)) : Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void removeById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public void removeAll() {
        users.clear();
        emails.clear();
    }

    @Override
    public void updateEmail(String email, String updatedEmail) {
        if (!emails.contains(email)) {
            throw new UserNotFoundException(email);
        }
        emails.remove(email);
        emails.add(updatedEmail);
    }

    public boolean containsEmail(final String email) {
        return emails.contains(email);
    }
}
