package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    public Category mapToModel(CategoryDto categoryDto);

    public CategoryDto mapToDto(Category category);
}
