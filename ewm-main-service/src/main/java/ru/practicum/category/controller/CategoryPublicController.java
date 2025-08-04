package ru.practicum.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryPublicService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicController {

    private final CategoryPublicService publicService;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на получение списка всех категорий, " +
                "from = {}," +
                "size = {}", from, size);
        return publicService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable @Positive(message = "Id категории должен быть больше нуля") Integer catId) {
        log.info("Получен запрос на получение категории по id = {}", catId);
        return publicService.findById(catId);
    }
}
