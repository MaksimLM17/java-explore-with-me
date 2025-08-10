package ru.practicum.events.service.admin;

import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.SearchRequestDto;
import ru.practicum.events.dto.UpdateAdminEventDto;

import java.util.List;

public interface EventAdminService {

    List<EventDto> searchEventsByParams(SearchRequestDto params);

    EventDto updateAdmin(Integer eventId, UpdateAdminEventDto updateAdminEventDto);
}
