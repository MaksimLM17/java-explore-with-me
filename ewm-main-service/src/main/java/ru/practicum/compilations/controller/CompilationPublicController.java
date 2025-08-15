package ru.practicum.compilations.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.open.CompilationPublicService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {

    private final CompilationPublicService publicService;

    @GetMapping
    public List<CompilationDto> getAll(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        log.info("""
                Получен запрос на получение списка подборок событий с параметрами:\s
                pinned = {}
                from = {}
                size = {}""", pinned, from, size);
        return publicService.getAll(pinned, from, size, request);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @Positive Integer compId, HttpServletRequest request) {
        log.info("Получен запрос на получение подборки событий с id = {}", compId);
        return publicService.getCompilationById(compId, request);
    }
}
