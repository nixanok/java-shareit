package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.tool.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        return UserMapper.toDto(userRepository.add(UserMapper.fromDto(user)));
    }

    @Override
    public UserDto patch(Long id, UserDto patchUser) {
        Optional<User> optionalUser = userRepository.getById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User user = optionalUser.get();
        if (patchUser.getEmail() != null) {
            Optional<User> userWithSameEmail  = userRepository.getByEmail(patchUser.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new EmailAlreadyExistException(patchUser.getEmail());
            }
        }
        if (patchUser.getEmail() != null) {
            userRepository.updateEmail(user.getEmail(), patchUser.getEmail());
            user.setEmail(patchUser.getEmail());
        }
        if (patchUser.getName() != null) {
            user.setName(patchUser.getName());
        }
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = userRepository.getById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto getByEmail(String email) {
        Optional<User> user = userRepository.getByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException(email);
        }
        return UserMapper.toDto(user.get());
    }

    @Override
    public void removeById(Long id) {
        userRepository.removeById(id);
    }
}
