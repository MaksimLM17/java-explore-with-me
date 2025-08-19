package ru.practicum.comments.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentResponseDto;
import ru.practicum.comments.service.open.CommentPublicService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@Slf4j
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentPublicService commentPublicService;

    @GetMapping
    public List<CommentResponseDto> getAll(@PathVariable @Positive Integer eventId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на получение всех комментариев события - {}, from - {}, size - {}",
                eventId, from, size);
        return commentPublicService.getAll(eventId, from, size);
    }
}
