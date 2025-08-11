package ru.practicum.events.service.close;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestState;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

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
            throw new StateException("Изменять можно только отмененные или ожидающие модерации события!");
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

    @Override
    public List<ParticipationRequestDto> getAllRequestsForEvents(Integer userId, Integer eventId) {
        Event event = validateDataId(userId, eventId);

        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::mapToDto)
                .toList();
    }

    @Override
    public EventStatusUpdateResult updateRequests(Integer userId, Integer eventId,
                                                  EventStatusUpdateRequest eventStatusUpdateRequest) {
        Event event = validateDataId(userId, eventId);

        if (event.getState().equals(State.CANCELED)) {
            log.error("Попытка обновить статус заявки у отмененного события!");
            throw new StateException("Событие отменено! Нельзя обновить статус заявки на участие!");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(eventStatusUpdateRequest.getRequestIds());

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestState.PENDING) {
                log.error("Попытка обновить запрос с некорректным статусом: {}", request.getStatus());
                throw new StateException("Обрабатывать запросы можно только в статусе PENDING");
            }

            if (!request.getEvent().getId().equals(eventId)) {
                log.error("Передан запрос на участие для другого события! EventId = {}, request.getEventId = {}", eventId,
                        request.getEvent().getId());
                throw new ConflictException("Передан запрос на участие для другого события!");
            }
        }

        if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests())) {
            throw new ConflictException("Нет свободных мест для участия в данном событии.");
        }


        RequestState status = eventStatusUpdateRequest.getStatus();

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        int limit = event.getParticipantLimit();
        int confirmed = event.getConfirmedRequests();

        if (status.equals(RequestState.CONFIRMED)) {
            log.info("При обработке запросов на подтверждение значение limit = {}, confirmed = {}, количество запросов = {}",
                    limit, confirmed, requests.size());
            for (ParticipationRequest request : requests) {
                if (limit > confirmed) {
                    request.setStatus(RequestState.CONFIRMED);
                    confirmedRequests.add(request);
                    confirmed++;
                } else {
                    request.setStatus(RequestState.REJECTED);
                    rejectedRequests.add(request);
                }
            }
            savedConfirmedRequests(event, confirmedRequests.size());
        } else if (status.equals(RequestState.REJECTED)) {
            for (ParticipationRequest request : requests) {
                request.setStatus(RequestState.REJECTED);
                rejectedRequests.add(request);
            }
        }
        requestRepository.saveAll(requests);
        log.info("Запросы к событию = {}, обработаны в количестве = {}", eventId, requests.size());
        return new EventStatusUpdateResult(
                confirmedRequests.stream().map(requestMapper::mapToDto).toList(),
                rejectedRequests.stream().map(requestMapper::mapToDto).toList()
        );
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

    private void savedConfirmedRequests(Event event, int count) {
        event.setConfirmedRequests(event.getConfirmedRequests() + count);
        eventRepository.save(event);
        log.info("Количество подтвержденных заявок = {}, у события = {}", event.getConfirmedRequests(), event.getId());
    }
}
