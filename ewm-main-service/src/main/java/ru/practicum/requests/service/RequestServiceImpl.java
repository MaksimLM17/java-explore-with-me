package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.StateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.RequestState;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto create(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = %d, не найдено!".formatted(eventId)));

        validationCreated(user, event);

        ParticipationRequest participationRequest;

        if (!event.isRequestModeration()) {
            participationRequest = createNewRequest(user, event, RequestState.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);

            eventRepository.save(event);
            log.info("Добавлен пользователь с id ={}, для участия в событии с id = {}", userId, eventId);
            log.info("Новое количество участников в событии = {}", event.getConfirmedRequests());

        } else {
          participationRequest = createNewRequest(user, event, RequestState.PENDING);
        }

        return requestMapper.mapToDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));

        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::mapToDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancelRequestId(Integer userId, Integer requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));
        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = %s, не найден!".formatted(requestId)));

        if (!participationRequest.getStatus().equals(RequestState.PENDING)) {
            log.error("Попытка отменить запрос со статусом: {}", participationRequest.getStatus());
            throw new StateException("Отменить запрос можно только со статусом в ожидании!");
        }

        participationRequest.setStatus(RequestState.CANCELED);
        log.info("Успешная отмена запрос с id = {}", requestId);
        return requestMapper.mapToDto(requestRepository.save(participationRequest));
    }

    private void validationCreated(User user, Event event) {
        Integer eventId = event.getId();
        Integer userId = user.getId();

        if (event.getInitiator().getId().equals(userId)) {
            log.error("Попытка добавить запрос на участие создателем события!");
            throw new ConflictException("Нельзя участвовать в собственном событии!");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("Попытка записаться на событие со статусом: {}", event.getState());
            throw new StateException("Событие не опубликовано!");
        }

        if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests()) && event.getParticipantLimit() != 0) {
            log.error("Попытка добавить запрос на участие, в котором превышен лимит участников!");
            throw new ConflictException("Превышен лимит участников!");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            log.error("Пользователь с id = {} уже отправлял запрос на участие в событии id = {}", userId, eventId);
            throw new ConflictException("Вы уже подавали запрос на участие в этом событии");
        }
    }

    private ParticipationRequest createNewRequest(User user, Event event, RequestState state) {
        ParticipationRequest participationRequest = requestRepository.save(ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(state)
                .build());
        log.info("Запрос с id = {}, добавлен со статусом: {}", participationRequest.getId(),
                participationRequest.getStatus());
        return  participationRequest;
    }
}
