package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.events.model.Event;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Page<Event> findAllByInitiatorId(Integer userId, Pageable pageable);
}
