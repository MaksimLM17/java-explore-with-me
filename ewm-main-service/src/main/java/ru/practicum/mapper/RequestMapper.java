package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto mapToDto(ParticipationRequest participationRequest);

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    ParticipationRequest mapToModel(ParticipationRequestDto participationRequestDto);
}
