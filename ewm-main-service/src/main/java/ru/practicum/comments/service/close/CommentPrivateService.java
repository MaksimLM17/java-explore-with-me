package ru.practicum.comments.service.close;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentRequestDto;

public interface CommentPrivateService {

    CommentDto create(CommentRequestDto commentRequestDto, Integer eventId, Integer userId);

    CommentDto update(CommentRequestDto commentRequestDto, Long commentId, Integer userId);

    void delete(Long commentId, Integer userId);
}
