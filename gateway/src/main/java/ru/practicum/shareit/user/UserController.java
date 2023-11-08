package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.practicum.shareit.user.dto.BasicInfo;
import ru.practicum.shareit.user.dto.EmailInfo;
import ru.practicum.shareit.user.dto.PatchInfo;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> userById(@Positive @PathVariable int userId) {
        log.info("Получен GET-запрос к эндпоинту /users/{userId} на получение пользователя по id.");
        return userClient.userById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> allUsers() {
        log.info("Получен GET-запрос к эндпоинту /users на получение всех пользователей.");
        return userClient.allUsers();
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({ BasicInfo.class, EmailInfo.class }) @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос к эндпоинту /users на добавление пользователя.");
        return userClient.createUser(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated({ PatchInfo.class }) UserDto userDto,
                                             @Positive @PathVariable int userId) {
        log.info("Получен PATCH-запрос к эндпоинту /users/{userId} на обновление пользователя по id.");
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable int userId) {
        log.info("Получен DELETE-запрос к эндпоинту /users/{userId} на удаление пользователя.");
        return userClient.deleteUser(userId);
    }
}
