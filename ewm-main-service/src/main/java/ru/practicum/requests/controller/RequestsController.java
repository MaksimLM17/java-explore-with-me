package ru.practicum.requests.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestsController {

    private final RequestService requestService;
    private static final String MESSAGE_ERROR_ID = "Id не должно быть меньше единицы!";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer userId,
                                          @RequestParam @Positive Integer eventId) {
        log.info("""
                Получен запрос на создание нового запроса на участие в событии, с данными:\s
                userId = {}\s
                eventId = {}\s
                """, userId, eventId);
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestsUserId(@PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer userId) {
        log.info("Получен запрос на получение запросов пользователя с id = {}, на участие в чужих событиях", userId);
        return requestService.getRequestsUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestId(@PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer userId,
                                                   @PathVariable @Positive(message = MESSAGE_ERROR_ID) Integer requestId) {
        log.info("""
                Получен запрос на отмену запроса на участие с данными\s
                userId = {}\s
                requestId = {}""", userId, requestId);

        return requestService.cancelRequestId(userId, requestId);
    }
}
