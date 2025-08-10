package ru.practicum.events.service.close;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventDto;

import java.util.List;

public interface EventPrivateService {

    EventDto create(NewEventDto newEventDto, Integer userId);

    List<EventDto> getAllEventsUser(Integer userId, Integer from, Integer size);

    EventDto getById(Integer userId, Integer eventId);

    EventDto update(Integer userId, Integer eventId, UpdateEventDto updateEventDto);
}
