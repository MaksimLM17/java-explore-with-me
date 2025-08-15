package ru.practicum.events.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.events.model.Event;
import ru.practicum.events.state.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Event> findEventsByAdminFilters(List<Integer> users, List<State> states,
                                                List<Integer> categories, LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);

        // Список условий для WHERE
        List<Predicate> predicates = new ArrayList<>();

        // Фильтр по пользователям
        if (users != null && !users.isEmpty()) {
            predicates.add(event.get("initiator").get("id").in(users));
        }

        // Фильтр по состояниям
        if (states != null && !states.isEmpty()) {
            predicates.add(event.get("state").in(states));
        }

        // Фильтр по категориям
        if (categories != null && !categories.isEmpty()) {
            predicates.add(event.get("category").get("id").in(categories));
        }

        // Фильтр по дате начала
        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(event.get("eventDate"), rangeStart));
        }

        // Фильтр по дате окончания
        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(event.get("eventDate"), rangeEnd));
        }

        // Применяем все условия
        cq.where(predicates.toArray(new Predicate[0]));

        // Сортировка и пагинация
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                if (order.isAscending()) {
                    orders.add(cb.asc(event.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(event.get(order.getProperty())));
                }
            }
            cq.orderBy(orders);
        }

        // Создаем запрос с пагинацией
        TypedQuery<Event> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}

