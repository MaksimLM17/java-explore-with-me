package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.ViewStatsRequestDto;
import ru.practicum.service.StatisticService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {

    private final StatisticService service;

    @PostMapping("/hit")
    public EndpointHitDto create(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return service.create(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@ModelAttribute ViewStatsRequestDto requestDto) {
        return service.getStats(requestDto.getStart(), requestDto.getEnd(), requestDto.getUris(), requestDto.isUnique());
    }
}
