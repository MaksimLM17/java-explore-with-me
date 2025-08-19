package ru.practicum.comments.service.open;

import ru.practicum.comments.dto.CommentResponseDto;

import java.util.List;

public interface CommentPublicService {
    List<CommentResponseDto> getAll(Integer eventId, Integer from, Integer size);
}
