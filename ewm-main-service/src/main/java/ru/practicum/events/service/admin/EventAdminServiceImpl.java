package ru.practicum.events.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.SearchRequestDto;
import ru.practicum.events.dto.UpdateAdminEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.state.State;
import ru.practicum.events.state.StateAction;
import ru.practicum.exception.EventStateException;
import ru.practicum.exception.InvalidDateTimeException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventDto> searchEventsByParams(SearchRequestDto params) {
        int pageNumber = (int) Math.floor((double) params.getFrom() / params.getSize());
        Pageable pageable = PageRequest.of(pageNumber, params.getSize());

        List<State> eventStates = null;
        if (params.getStates() != null) {
            eventStates = params.getStates().stream()
                    .map(State::valueOf)
                    .toList();
        }

        List<Event> events = eventRepository.findEventsByAdminFilters(
                params.getUsers(),
                eventStates,
                params.getCategories(),
                params.getRangeStart(),
                params.getRangeEnd(),
                pageable
        );

        return events.stream()
                .map(eventMapper::mapToDto)
                .toList();
    }

    @Override
    public EventDto updateAdmin(Integer eventId, UpdateAdminEventDto updateAdminEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = %d, не найдено!".formatted(eventId)));

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidDateTimeException("Начало события меньше чем через час!");
        }

        if ((event.getState().equals(State.CANCELED) || (event.getState().equals(State.PUBLISHED))) ) {
            log.error("Попытка публикации события со статусом: {}", event.getState());
            throw new EventStateException("Нельзя опубликовать отмененное или опубликованное событие!");
        }

        if (updateAdminEventDto.getStateAction() != null) {
            StateAction updateState = updateAdminEventDto.getStateAction();

            switch (updateState) {
                case PUBLISH_EVENT -> {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(State.CANCELED);
                default -> throw new EventStateException("Некорректный статус: %s".formatted(updateState));
            }
        }

        eventMapper.mapToModelFromUpdateAdminDto(updateAdminEventDto, event);
        log.info("Событие с id = {}, обновлено со статусом: {}", event.getId(), event.getState());
        return eventMapper.mapToDto(eventRepository.save(event));
    }
}
