package ru.practicum.events.service.open;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.SearchPublishedEvents;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.state.State;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.StateException;
import ru.practicum.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventDto> getAll(SearchPublishedEvents publishedEvents, HttpServletRequest request) {
        log.info("Данные запроса: {}, {}", request.getRequestURI(), request.getRemoteAddr());
        saveStatistics(request);

        Specification<Event> spec = Specification.where(EventSpecifications.isPublished());

        if (publishedEvents.getText() != null && !publishedEvents.getText().isBlank()) {
            spec = spec.and(EventSpecifications.containsText(publishedEvents.getText()));
        }

        if (publishedEvents.getCategories() != null && !publishedEvents.getCategories().isEmpty()) {
            spec = spec.and(EventSpecifications.hasCategories(publishedEvents.getCategories()));
        }

        if (publishedEvents.getPaid() != null) {
            spec = spec.and(EventSpecifications.isPaid(publishedEvents.getPaid()));
        }

        if (publishedEvents.getRangeStart() != null) {
            spec = spec.and(EventSpecifications.eventDateAfter(publishedEvents.getRangeStart()));
        } else {
            spec = spec.and(EventSpecifications.eventDateAfter(LocalDateTime.now()));
        }

        if (publishedEvents.getRangeEnd() != null) {
            spec = spec.and(EventSpecifications.eventDateBefore(publishedEvents.getRangeEnd()));
        }

        if (publishedEvents.getOnlyAvailable()) {
            spec = spec.and(EventSpecifications.isAvailable());
        }

        Sort sort = buildSort(publishedEvents.getSort());

        Pageable pageable = PageRequest.of(publishedEvents.getFrom() / publishedEvents.getSize(),
                publishedEvents.getSize(),
                sort);

        List<Event> events = eventRepository.findAll(spec, pageable).getContent();


        // Получаем статистику просмотров для всех событий
        Map<Integer, Long> views = getViewsForEvents(events, request);

        // Преобразуем в DTO и устанавливаем просмотры
        return events.stream()
                .map(eventMapper::mapToDto)
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .toList();
    }

    @Override
    public EventDto getById(Integer id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = %d, не найдено!".formatted(id)));

        if (event.getState() != State.PUBLISHED) {
            log.error("Попытка получить информацию о неопубликованном событии со статусом: {}, eventId = {}",
                    event.getState(), id);
            throw new StateException("Событие должно быть опубликовано!");
        }
        saveStatistics(request);

        EventDto eventDto = eventMapper.mapToDto(event);
        eventDto.setViews(getStats(event.getId(), request));
        return eventDto;
    }

    private void saveStatistics(HttpServletRequest request) {
        statsClient.create(request);
    }

    private Long getStats(Integer eventId, HttpServletRequest request) {
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();
        String uri = "/events/" + eventId;
        try {
            ResponseEntity<Object> response = statsClient.getStats(start, end, List.of(uri), true);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<ViewStatsDto> views = parseStatsResponse(response.getBody());
                return views.isEmpty() ? 0L : views.getFirst().getHits();
            }
        } catch (Exception e) {
            log.error("Ошибка при получении статистики для события {}: {}", eventId, e.getMessage());
            throw new BadRequestException("Ошибка получения статистики!");
        }
        return 0L;
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

    private Sort buildSort(String sortParam) {
        if (sortParam == null || sortParam.equals("EVENT_DATE")) {
            return Sort.by("eventDate").descending();
        } else if (sortParam.equals("VIEWS")) {
            return Sort.by("views").descending();
        }
        return Sort.by("eventDate").descending();
    }

    private Map<Integer, Long> getViewsForEvents(List<Event> events, HttpServletRequest request) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();

        try {
            ResponseEntity<Object> response = statsClient.getStats(start, end, uris, true);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<ViewStatsDto> stats = parseStatsResponse(response.getBody());
                return stats.stream()
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.getUri()),
                                ViewStatsDto::getHits
                        ));
            }
        } catch (Exception e) {
            log.error("Ошибка при получении статистики для списка событий: {}", e.getMessage());
        }

        return Collections.emptyMap();
    }

    private Integer extractEventIdFromUri(String uri) {
        try {
            return Integer.parseInt(uri.substring(uri.lastIndexOf('/') + 1));
        } catch (Exception e) {
            log.error("Не удалось извлечь ID события из URI: {}", uri);
            return null;
        }
    }
}
