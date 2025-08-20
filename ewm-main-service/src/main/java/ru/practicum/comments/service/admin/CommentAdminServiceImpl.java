package ru.practicum.comments.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentStatus;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.StateException;
import ru.practicum.mapper.CommentMapper;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentAdminServiceImpl  implements CommentAdminService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto moderateStatus(Long commentId, String status) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = %d, не найден!".formatted(commentId)));

        CommentStatus commentStatus;
        try {
            commentStatus = CommentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Передан несуществующий статус комментария: {}", status);
            throw new StateException("Недопустимый статус комментария. Допустимые значения: "
                    + Arrays.toString(CommentStatus.values()));
        }

        if (!(commentStatus.equals(CommentStatus.PUBLISHED) || commentStatus.equals(CommentStatus.REJECTED))) {
            log.error("Попытка использовать некорректный статус модерации = {}", commentStatus);
            throw new StateException("Некорректный статус модерации комментария!");

        }
        comment.setStatus(commentStatus);
        Comment savedComment = commentRepository.save(comment);
        log.info("Статус комментария {} изменён на {}", commentId, commentStatus);

        return commentMapper.mapToDto(savedComment);
    }
}
