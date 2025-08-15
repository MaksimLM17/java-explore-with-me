package ru.practicum.compilations.service.open;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import ru.practicum.compilations.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {

    List<CompilationDto> getAll(Boolean pinned, int from, int size, HttpServletRequest request);

    CompilationDto getCompilationById(Integer compId, HttpServletRequest request);
}
