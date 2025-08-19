package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.SearchPublishedEvents;
import ru.practicum.events.service.open.EventPublicService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {

    private final EventPublicService publicService;

    @GetMapping
    public List<EventDto> getAll(@ModelAttribute @Valid SearchPublishedEvents publishedEvents,
                                 HttpServletRequest request) {
        log.info("Получен запрос на поиск событий по параметрам: \n" +
                "{}", publishedEvents);
        return publicService.getAll(publishedEvents, request);
    }

    @GetMapping("/{id}")
    public EventDto getById(@PathVariable @Positive(message = "Id не должно быть меньше единицы!") Integer id,
                            HttpServletRequest request) {
        log.info("Получен запрос на получение события = {}, public", id);
        return publicService.getById(id, request);
    }
}
