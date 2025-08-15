package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.service.admin.CompilationAdminService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationAdminService adminService;
    private final static String MESSAGE_ERROR_ID = "Id не должно быть меньше единицы!";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен запрос на создание подборки событий с данными: {}", newCompilationDto);
        return adminService.create(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(
            @PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer compId,
            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.info("Получен запрос на обновление подборки событий с данными: {}", updateCompilationDto);
        return adminService.update(compId, updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer compId) {
        log.info("Получен запрос на удаление подборки событий с id: {}", compId);
        adminService.delete(compId);
    }
}
