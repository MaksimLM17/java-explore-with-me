package ru.practicum.compilations.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminServiceImpl implements  CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;


    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toModelFromNew(newCompilationDto);
        Set<Event> events = new HashSet<>();

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events.addAll(eventRepository.findAllById(newCompilationDto.getEvents()));
        }

        compilation.setEvents(events);
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Подборка сохранена с id: {}", savedCompilation.getId());
        return compilationMapper.mapToDto(savedCompilation);
    }

    @Override
    public CompilationDto update(Integer compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка c id = %d, не найдена!".formatted(compId)));
        compilationMapper.mapToModelFromUpdate(updateCompilationDto, compilation);

        if (updateCompilationDto.getEvents() != null && !updateCompilationDto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationDto.getEvents()));
            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);
        log.info("Подборка  обновлена с id: {}", compilation.getId());
        return compilationMapper.mapToDto(compilation);
    }

    @Override
    public void delete(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка c id = %d, не найдена!".formatted(compId)));
        log.info("Подборка  удалена с id: {}", compId);
        compilationRepository.delete(compilation);
    }
}
