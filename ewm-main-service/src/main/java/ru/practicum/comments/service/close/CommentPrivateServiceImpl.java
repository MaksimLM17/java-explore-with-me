package ru.practicum.comments.service.close;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentRequestDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentStatus;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.state.State;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.StateException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateServiceImpl implements CommentPrivateService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto create(CommentRequestDto commentRequestDto, Integer eventId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = %d, не найдено!".formatted(eventId)));
        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("Попытка создать комментарий к неопубликованному событию!");
            throw new StateException("Добавить комментарий можно только к опубликованному событию");
        }
        Comment comment = Comment.builder()
                .text(commentRequestDto.getText())
                .author(user)
                .event(event)
                .createdOn(LocalDateTime.now())
                .status(CommentStatus.PENDING)
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий сохранен под id = {}", savedComment.getId());
        return commentMapper.mapToDto(savedComment);
    }

    @Override
    public CommentDto update(CommentRequestDto commentRequestDto, Long commentId, Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId));
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = %d, не найден!".formatted(commentId)));

        validateAuthorComment(comment, userId);

        comment.setText(commentRequestDto.getText());
        comment.setStatus(CommentStatus.EDITED);
        comment.setEditedOn(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий с id = {} обновлен пользователем с id = {}", commentId, userId);
        return commentMapper.mapToDto(savedComment);
    }

    @Override
    public void delete(Long commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = %d, не найден!".formatted(commentId)));

        validateAuthorComment(comment, userId);

        commentRepository.delete(comment);
        log.info("Комментарий с id = {} удален пользователем с id = {}", commentId, userId);
    }

    private void validateAuthorComment(Comment comment, Integer userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            log.error("Попытка обновить комментарий не автором! commentId = {}, userId = {}", comment.getId(), userId);
            throw new ForbiddenException("Обновлять комментарии может только автор!");
        }
    }
}
