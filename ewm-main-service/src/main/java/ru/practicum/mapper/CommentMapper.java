package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentResponseDto;
import ru.practicum.comments.model.Comment;

@Mapper(componentModel = "spring", uses = {EventMapper.class, UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    CommentDto mapToDto(Comment comment);

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto mapToResponseForGetAll(Comment comment);
}
