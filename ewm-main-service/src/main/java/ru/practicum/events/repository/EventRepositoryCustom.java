package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {

    List<Event> findEventsByAdminFilters(List<Integer> users, List<State> states,
                                         List<Integer> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Pageable pageable);
}
