package ru.practicum.comments.service.admin;

import ru.practicum.comments.dto.CommentDto;

public interface CommentAdminService {

    CommentDto moderateStatus(Long commentId, String status);
}
