package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateUser_Successful() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        when(userService.create(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"JohnDoe\", \"email\": \"john@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("JohnDoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).create(userDto);
    }

    @Test
    public void testPatchUser_Successful() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("john@example.com")
                .build();

        when(userService.patch(1L, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).patch(1L, userDto);
    }

    @Test
    public void testGetUsers_Successful() throws Exception {
        UserDto user1 = UserDto.builder()
                .name("JohnDoe")
                .email("john@example.com")
                .build();
        UserDto user2 = UserDto.builder()
                .name("JaneSmith")
                .email("jane@example.com")
                .build();

        List<UserDto> users = Arrays.asList(user1, user2);

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("JohnDoe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].name").value("JaneSmith"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void testGetUsers_EmptyList() throws Exception {
        List<UserDto> emptyList = Collections.emptyList();

        when(userService.getAll()).thenReturn(emptyList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService, times(1)).getAll();
    }

    @Test
    public void testGetUser_Successful() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getById(1L);
    }

    @Test
    public void testGetUser_NotFound() throws Exception {
        when(userService.getById(1L)).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.description").value("User with id = 1 not found."));

        verify(userService, times(1)).getById(1L);
    }

    @Test
    public void testRemoveUserById_Successful() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).removeById(1L);
    }

    @Test
    public void testRemoveUserById_UserNotFound() throws Exception {
        Mockito.doThrow(UserNotFoundException.class).when(userService).removeById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).removeById(1L);
    }

}


