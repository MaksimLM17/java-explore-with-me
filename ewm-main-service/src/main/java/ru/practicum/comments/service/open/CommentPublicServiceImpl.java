package ru.practicum.comments.service.open;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentResponseDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentStatus;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentPublicServiceImpl implements CommentPublicService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentResponseDto> getAll(Integer eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id = %d не найдено".formatted(eventId));
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());

        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageable);
        log.info("Отправлен список комментариев размером: {}", comments.size());
        return comments.stream()
                .map(commentMapper::mapToResponseForGetAll)
                .toList();
    }
}
