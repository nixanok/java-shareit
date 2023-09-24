package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User add(User user);


    boolean contains(final Long id);

    Optional<User> getById(final Long id);

    Optional<User> getByEmail(String email);

    List<User> getAll();

    void removeById(final Long id);

    void removeAll();

    void updateEmail(Long id, String email);
}
