package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentRequestDto;
import ru.practicum.comments.service.close.CommentPrivateService;

@RestController
@RequestMapping("/events/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {

    private final CommentPrivateService commentPrivateService;
    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";

    @PostMapping("{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@RequestBody @Valid CommentRequestDto commentRequestDto,
                             @PathVariable @Positive Integer eventId,
                             @RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        log.info("Получен запрос на создание комментария для события - {}, пользователем - {}, с данными: {}",
                eventId, userId,commentRequestDto);
        return commentPrivateService.create(commentRequestDto, eventId, userId);
    }

    @PutMapping("{commentId}")
    public CommentDto update(@RequestBody @Valid CommentRequestDto commentRequestDto,
                             @PathVariable @Positive Long commentId,
                             @RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        log.info("Получен запрос на обновление комментария - {}, пользователем - {}, с данными: {}",
                commentId, commentRequestDto, userId);
        return commentPrivateService.update(commentRequestDto, commentId, userId);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long commentId,
                       @RequestHeader(USER_ID_IN_HEADER) @Positive Integer userId) {
        log.info("Получен запрос на удаление комментария - {}, пользователем - {}",
                commentId, userId);
        commentPrivateService.delete(commentId, userId);
    }

}
