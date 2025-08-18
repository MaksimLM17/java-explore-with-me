package ru.practicum.comments.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.admin.CommentAdminService;

@RestController
@RequestMapping("/admin/comments/{commentId}")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @PatchMapping
    public CommentDto moderateStatus(@PathVariable @Positive Long commentId,
                                     @RequestParam @NotBlank String status) {
        log.info("Получен запрос на модерацию комментария = {}, состояние действия: {}", commentId, status);
        return commentAdminService.moderateStatus(commentId, status);
    }
}
