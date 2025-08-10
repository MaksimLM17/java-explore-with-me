package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.requests.model.ParticipationRequest;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    boolean existsByEventIdAndRequesterId(Integer eventId, Integer requesterId);
}
