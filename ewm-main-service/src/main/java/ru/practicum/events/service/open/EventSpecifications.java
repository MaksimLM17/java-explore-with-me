package ru.practicum.events.service.open;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> isPublished() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), State.PUBLISHED);
    }

    public static Specification<Event> containsText(String text) {
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + text.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Event> hasCategories(List<Integer> categories) {
        return (root, query, criteriaBuilder) ->
                root.get("category").get("id").in(categories);
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> eventDateAfter(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), dateTime);
    }

    public static Specification<Event> eventDateBefore(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), dateTime);
    }

    public static Specification<Event> isAvailable() {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("participantLimit"), 0),
                cb.lessThan(
                        root.get("confirmedRequests"),
                        root.get("participantLimit")
                )
        );
    }
}
