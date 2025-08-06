package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.service.close.EventsPrivateService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class EventsPrivateController {

    private final EventsPrivateService eventsPrivateService;
    private final static String MESSAGE_ERROR_ID = "Id пользователя не должно быть меньше единицы!";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@RequestBody @Valid NewEventDto newEventDto,
                           @PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer userId) {
        log.info("Получен запрос на добавление нового события: {}" +
                " пользователем: {}", newEventDto, userId);
        return eventsPrivateService.create(newEventDto, userId);
    }

    @GetMapping
    public List<EventDto> getAllEventsUser(@PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("""
                Получен запрос на получение всех событий пользователя: {}\s
                from = {}\s
                size = {}""", userId, from, size);
        return eventsPrivateService.getAllEventsUser(userId, from, size);
    }

}
