package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryAdminService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {

    private final CategoryAdminService adminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Получен запрос на создание новой категории: {}", categoryDto.getName());
        return adminService.create(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "Id категории должен быть больше нуля") Integer catId) {
        log.info("Получен запрос на удаление категории с id = {}", catId);
        adminService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDto,
                              @PathVariable @Positive(message = "Id категории должен быть больше нуля") Integer catId) {
        log.info("Получен запрос на обновление категории с id = {}, новое название: {}",catId, categoryDto.getName());
        return adminService.update(categoryDto, catId);
    }
}
