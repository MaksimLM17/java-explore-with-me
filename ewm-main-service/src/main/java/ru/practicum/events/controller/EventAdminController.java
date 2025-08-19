package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.SearchRequestDto;
import ru.practicum.events.dto.UpdateAdminEventDto;
import ru.practicum.events.service.admin.EventAdminService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {

    private final EventAdminService adminService;

    @GetMapping
    public List<EventDto> searchEventsByParams(@ModelAttribute @Valid SearchRequestDto params) {
        log.info("Получен запрос на получение списка событий по параметрам: \n" +
                "{}", params);
        return adminService.searchEventsByParams(params);
    }

    @PatchMapping("{eventId}")
    public EventDto updateAdmin(@PathVariable @Positive(message = "Id не должно быть меньше единицы!") Integer eventId,
                                @RequestBody @Valid UpdateAdminEventDto updateAdminEventDto) {
        log.info("""
                Получен запрос на обновление события администратором с данными:\s
                eventId = {}
                EventDto = {}""", eventId, updateAdminEventDto);
        return adminService.updateAdmin(eventId, updateAdminEventDto);
    }
}
