package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {
        log.debug("Request \"createUser\"is called.");
        return userService.create(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto patchUser(@PathVariable(name = "userId") long id,
                             @RequestBody final UserDto user) {
        log.debug("Request \"patchUser\"is called.");
        return userService.patch(id, user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.debug("Request \"getUsers\"is called.");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable(name = "userId") long id) {
        log.debug("Request \"getUser\"is called.");
        return userService.getById(id);
    }

    @DeleteMapping("/{userId}")
    public void removeUserById(@PathVariable(name = "userId") long id) {
        log.debug("Request \"removeUserById\"is called.");
        userService.removeById(id);
    }

}
