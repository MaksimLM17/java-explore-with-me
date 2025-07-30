package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStat;

@Mapper(componentModel = "spring")
public interface StatMapper {

    public EndpointHitDto mapToDtoEndpoint(EndpointHit endpointHit);

    public EndpointHit mapToModelEndpoint(EndpointHitDto endpointHitDto);

    public ViewStatsDto mapToDtoView(ViewStat viewStat);

    public ViewStat mapToModelView(ViewStatsDto viewStatsDto);


}
