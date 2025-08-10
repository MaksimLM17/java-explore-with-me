package ru.practicum.events.service.close;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.EventStateException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.InvalidDateTimeException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    public EventDto create(NewEventDto newEventDto, Integer userId) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = %d, не найдена!"
                        .formatted(newEventDto.getCategory())));

        validateDate(newEventDto.getEventDate());

        Event event = eventMapper.mapToModel(newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        EventDto eventDto = eventMapper.mapToDto(eventRepository.save(event));
        log.info("Событие создано: {}", eventDto);

        return eventDto;
    }

    @Override
    public List<EventDto> getAllEventsUser(Integer userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));

        int pageNumber = (int) Math.floor((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(eventMapper::mapToDto)
                .toList();
    }

    @Override
    public EventDto getById(Integer userId, Integer eventId) {
        Event event = validateDataId(userId, eventId);

        log.info("Данный отправленного события созданного текущим пользователем: {}", event);

        return eventMapper.mapToDto(event);
    }

    @Override
    public EventDto update(Integer userId, Integer eventId, UpdateEventDto updateEventDto) {
        Event event = validateDataId(userId, eventId);

        if (updateEventDto.getEventDate() != null) {
            validateDate(updateEventDto.getEventDate());
        }

        if (event.getState().equals(State.PUBLISHED)) {
            log.error("Попытка изменить событие со статусом {}", event.getState());
            throw new EventStateException("Изменять можно только отмененные или ожидающие модерации события!");
        }

        if (updateEventDto.getStateAction() != null) {
            switch (updateEventDto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
            }
        }

        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id = %d, не найдена!"
                            .formatted(updateEventDto.getCategory())));
            event.setCategory(category);
        }

        eventMapper.mapToModelFromUpdate(updateEventDto, event);
        log.info("Событие обновлено с данными: {}", event);
        return eventMapper.mapToDto(eventRepository.save(event));
    }

    private Event validateDataId(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = %d, не найдено!".formatted(eventId)));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Данный пользователь не является создателем события!");
        }
        return event;
    }

    private void validateDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Начало события не раньше чем через два часа с момента создания!");
            throw new InvalidDateTimeException("Начало события должно быть не раньше чем через два часа с момента создания!");
        }
    }
}
