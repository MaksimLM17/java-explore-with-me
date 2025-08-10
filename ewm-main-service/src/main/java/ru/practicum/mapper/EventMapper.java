package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateAdminEventDto;
import ru.practicum.events.dto.UpdateEventDto;
import ru.practicum.events.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    Event mapToModel(NewEventDto newEventDto);

    @Mapping(target = "createdOn", source = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventDto mapToDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void mapToModelFromUpdate(UpdateEventDto updateEventDto, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void mapToModelFromUpdateAdminDto(UpdateAdminEventDto updateAdminEventDto, @MappingTarget Event event);

}
