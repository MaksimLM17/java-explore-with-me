package ru.practicum.category.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UniqueConflictException;
import ru.practicum.mapper.CategoryMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        hasName(categoryDto.getName());
        Category category = categoryRepository.save(categoryMapper.mapToModel(categoryDto));
        log.info("Категория сохранена с данными: {}", category);
        return categoryMapper.mapToDto(category);
    }

    @Override
    public void delete(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            log.error("Категория с id = {}, не найдена!", catId);
            throw new NotFoundException("Категория по id = %d не найдена".formatted(catId));
        }

        if (eventRepository.existsByCategoryId(catId)) {
            log.error("Попытка удалить категорию к которой привязаны события! Id = {}", catId);
            throw new ConflictException("К данной категории привязаны события!");
        }
        categoryRepository.deleteById(catId);
        log.error("Категория с id = {}, удалена!", catId);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, Integer catId) {
        Category category = categoryRepository.findById(catId)
                        .orElseThrow(() -> new NotFoundException("Категория по id = %d не найдена".formatted(catId)));
        log.info("Данные найденной категории: {}", category);

        if (category.getName().equalsIgnoreCase(categoryDto.getName())) {
            return categoryMapper.mapToDto(category);
        }

        hasName(categoryDto.getName());
        categoryDto.setId(catId);
        Category categoryUp = categoryRepository.save(categoryMapper.mapToModel(categoryDto));
        log.info("Категория обновлена с параметрами: {}", categoryUp);
        return categoryMapper.mapToDto(categoryUp);
    }

    private void hasName(String name) {
        if (categoryRepository.existsByName(name)) {
            log.error("Такая категория уже существует! name = {}", name);
            throw new UniqueConflictException("Категория с таким именем уже существует");
        }
    }
}
