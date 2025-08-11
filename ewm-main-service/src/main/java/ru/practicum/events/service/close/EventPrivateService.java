package ru.practicum.events.service.close;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.events.dto.*;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {

    EventDto create(NewEventDto newEventDto, Integer userId);

    List<EventDto> getAllEventsUser(Integer userId, Integer from, Integer size);

    EventDto getById(Integer userId, Integer eventId);

    EventDto update(Integer userId, Integer eventId, UpdateEventDto updateEventDto);

    List<ParticipationRequestDto> getAllRequestsForEvents(Integer userId, Integer eventId);

    EventStatusUpdateResult updateRequests(Integer userId, Integer eventId,EventStatusUpdateRequest eventStatusUpdateRequest);
}
