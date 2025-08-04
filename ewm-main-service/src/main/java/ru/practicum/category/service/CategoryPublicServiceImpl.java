package ru.practicum.category.service;

import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

@Service
public class CategoryPublicServiceImpl implements CategoryPublicService {

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        return List.of();
    }

    @Override
    public CategoryDto findById(Integer catId) {
        return null;
    }
}
