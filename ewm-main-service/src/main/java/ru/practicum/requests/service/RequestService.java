package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto create(Integer userId, Integer eventId);

    List<ParticipationRequestDto> getRequestsUserId(Integer userId);

    ParticipationRequestDto cancelRequestId(Integer userId, Integer requestId);
}
