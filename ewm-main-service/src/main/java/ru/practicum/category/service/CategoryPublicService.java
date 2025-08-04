package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> findAll( Integer from, Integer size);

    CategoryDto findById(Integer catId);
}
