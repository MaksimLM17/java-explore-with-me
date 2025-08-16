package ru.practicum.compilations.service.open;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicServiceImpl implements  CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final StatsClient statsClient;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size, HttpServletRequest request) {
        int pageNumber = (int) Math.floor((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned,pageable);
        Map<Long, Integer> views = getAllViewsForCompilations(compilations);

        List<CompilationDto> compilationDtos = compilations.stream()
                        .map(compilationMapper::mapToDto)
                        .toList();

        compilationDtos.forEach(compilationDto -> {
            compilationDto.getEvents().forEach(eventDto -> {
                eventDto.setViews(views.getOrDefault(eventDto.getId(), 0));
            });
        });
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Integer compId, HttpServletRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка c id = %d, не найдена!".formatted(compId)));
        Map<Long, Integer> views = getAllViewsForCompilations(List.of(compilation));
        CompilationDto compilationDto = compilationMapper.mapToDto(compilation);

        compilationDto.getEvents().forEach(eventShortDto ->
                eventShortDto.setViews(views.getOrDefault(eventShortDto.getId(), 0)));

        return compilationDto;
    }

    private Map<Long, Integer> getAllViewsForCompilations(List<Compilation> compilations) {
        if (compilations == null || compilations.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Event> allEvents = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .toList();

        if (allEvents.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = allEvents.stream()
                .map(event -> "/events/" + event.getId())
                .distinct()
                .toList();

        LocalDateTime start = allEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusMonths(1));
        try {
            ResponseEntity<Object> response = statsClient.getStats(start, LocalDateTime.now(), uris, false);
            List<ViewStatsDto> stats = parseStatsResponse(response.getBody());
            return stats.stream().collect(Collectors.toMap(
                    s -> Long.parseLong(s.getUri().replace("/events/", "")),
                    s -> s.getHits().intValue()
            ));
        } catch (Exception e) {
            log.error("Ошибка при получении статистики для списка событий: {}", e.getMessage());
            throw new BadRequestException("Ошибка при получении статистики для списка событий");
        }
    }

    private List<ViewStatsDto> parseStatsResponse(Object body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    mapper.writeValueAsString(body),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            log.error("Ошибка парсинга статистики: {}", e.getMessage());
            return List.of();
        }
    }
}
