package ru.practicum.compilations.service.admin;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;

public interface CompilationAdminService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Integer compId, UpdateCompilationDto updateCompilationDto);

    void delete(Integer compId);
}
