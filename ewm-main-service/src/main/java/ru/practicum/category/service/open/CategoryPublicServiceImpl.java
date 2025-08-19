package ru.practicum.category.service.open;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicServiceImpl implements CategoryPublicService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        int pageNumber = (int) Math.floor((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Category> page = categoryRepository.findAll(pageable);

        return page.getContent().stream()
                .map(categoryMapper::mapToDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Integer catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория по id = %d не найдена".formatted(catId)));
        log.info("Отправлена категория: {}", category);
        return categoryMapper.mapToDto(category);
    }
}
