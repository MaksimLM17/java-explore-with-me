package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.events.location.Location;
import ru.practicum.events.location.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location mapToModel(LocationDto locationDto);

    LocationDto mapToDto(Location location);

}
