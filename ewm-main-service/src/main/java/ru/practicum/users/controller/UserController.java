package ru.practicum.users.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserRequestDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserShortDto userShortDto) {
        log.info("Получен запрос на создание нового пользователя: {}", userShortDto);
       return userService.create(userShortDto);
    }

    @GetMapping
    public List<UserDto> findByParameters(@Valid @ModelAttribute UserRequestDto userRequestDto) {
        log.info("Получен запрос на получение списка пользователей! С данными ids = {}," +
                "from = {}," +
                "size = {}", userRequestDto.getIds(), userRequestDto.getFrom(), userRequestDto.getSize());
        return userService.findByParameters(userRequestDto.getIds(), userRequestDto.getFrom(), userRequestDto.getSize());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @PositiveOrZero(message = "Id пользователя не может быть отрицательным") Integer userId) {
        log.info("Получен запрос на удаление пользователя: {}", userId);
        userService.delete(userId);
    }
}
