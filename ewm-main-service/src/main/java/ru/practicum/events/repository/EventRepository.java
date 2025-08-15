package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Page<Event> findAllByInitiatorId(Integer userId, Pageable pageable);

    boolean existsByCategoryId(Integer categoryId);

    //@Deprecated
    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart)" +
            "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> findEventsByAdminFilters(
            @Param("users") List<Integer> users,
            @Param("states") List<State> states,
            @Param("categories") List<Integer> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);
}
