package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user);

    UserDto patch(Long id, UserDto user);

    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto getByEmail(String email);

    void removeById(Long id);

}
