package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

public interface CategoryAdminService {

    CategoryDto create(CategoryDto categoryDto);

    void delete(Integer catId);

    CategoryDto update(CategoryDto categoryDto, Integer catId);
}
