package ru.practicum.events.service.close;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;

import java.util.List;

public interface EventsPrivateService {

    EventDto create(NewEventDto newEventDto, Integer userId);

    List<EventDto> getAllEventsUser(Integer userId, Integer from, Integer size);
}
