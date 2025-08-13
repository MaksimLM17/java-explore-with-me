package ru.practicum.events.service.open;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.SearchPublishedEvents;

import java.util.List;

public interface EventPublicService {

    List<EventDto> getAll(SearchPublishedEvents publishedEvents, HttpServletRequest request);

    EventDto getById(Integer id, HttpServletRequest request);
}
