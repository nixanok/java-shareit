package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.tool.UserMapper;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto testUser;

    private User existingUser;


    @BeforeEach
    public void setUp() {
        testUser = UserDto.builder()
                .email("test@example.com")
                .name("name")
                .build();

        existingUser = User.builder()
                .id(1L)
                .name("JohnDoe")
                .email("john@example.com")
                .build();
    }

    @Test
    public void testCreateUser_Successful() {
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.fromDto(testUser));

        UserDto createdUser = userService.create(testUser);

        assertEquals(testUser.getEmail(), createdUser.getEmail());
        assertEquals(testUser.getName(), createdUser.getName());
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("test@example.com"));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(testUser));
    }

    @Test
    public void testPatchUser_Successful() {
        Long userId = 1L;
        UserDto patchUser = UserDto.builder()
                .email("new_email@example.com")
                .name("JaneDoe")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(patchUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDto updatedUser = userService.patch(userId, patchUser);

        assertEquals(patchUser.getEmail(), updatedUser.getEmail());
        assertEquals(patchUser.getName(), updatedUser.getName());
    }

    @Test
    public void testPatchUser_UserNotFound() {
        Long userId = 2L;
        UserDto patchUser = UserDto.builder()
                .email("new_email@example.com")
                .name("JaneDoe")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.patch(userId, patchUser));
    }

    @Test
    public void testPatchUser_EmailAlreadyExists() {
        Long userId = 1L;
        UserDto patchUser = UserDto.builder()
                .email("new_email@example.com")
                .name("JaneDoe")
                .build();

        User someUserWithSameEmail = User.builder()
                .id(2L)
                .email("new_email@example.com")
                .name("JaneDoe")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(patchUser.getEmail())).thenReturn(Optional.of(someUserWithSameEmail));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.patch(userId, patchUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetAllUsers_Successful() {
        List<User> userList = Arrays.asList(
                User.builder().id(1L).name("JohnDoe").email("john@example.com").build(),
                User.builder().id(2L).name("JaneSmith").email("jane@example.com").build()
        );

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> expectedUserDtoList = userList.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        List<UserDto> actualUserDtoList = userService.getAll();

        assertEquals(expectedUserDtoList.size(), actualUserDtoList.size());
        assertEquals(expectedUserDtoList.get(0), actualUserDtoList.get(0));
        assertEquals(expectedUserDtoList.get(1), actualUserDtoList.get(1));
    }

    @Test
    public void testGetAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> actualUserDtoList = userService.getAll();

        assertEquals(0, actualUserDtoList.size());
    }

    @Test
    public void testGetUserById_Successful() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("JohnDoe")
                .email("john@example.com")
                .build();
        UserDto expectedUserDto = UserMapper.toDto(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getById(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    public void testGetUserById_UserNotFound() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    public void testGetUserByEmail_Successful() {
        String email = "john@example.com";
        User user = User.builder()
                .id(1L)
                .name("JohnDoe")
                .email(email)
                .build();
        UserDto expectedUserDto = UserMapper.toDto(user);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getByEmail(email);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        String email = "jane@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getByEmail(email));
    }

    @Test
    public void testRemoveUserById_Successful() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.removeById(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testRemoveUserById_UserNotFound() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.removeById(userId));
        verify(userRepository, never()).deleteById(userId);
    }

}


