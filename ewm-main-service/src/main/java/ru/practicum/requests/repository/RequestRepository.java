package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.requests.model.ParticipationRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    boolean existsByEventIdAndRequesterId(Integer eventId, Integer requesterId);

    List<ParticipationRequest> findByRequesterId(Integer requesterId);
}
