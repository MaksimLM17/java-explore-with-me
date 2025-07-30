package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatisticService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {

    private final StatisticService service;

    @PostMapping("/hits")
    public EndpointHitDto create(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return service.create(endpointHitDto);
    }

    @GetMapping("/events")
    public List<ViewStatsDto> getStats(@RequestParam LocalDateTime start,
                                       @RequestParam LocalDateTime end,
                                       @RequestParam List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }


}
