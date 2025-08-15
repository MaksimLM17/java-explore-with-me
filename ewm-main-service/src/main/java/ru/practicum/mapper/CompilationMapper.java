package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.model.Compilation;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto mapToDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    @Mapping(target = "id", ignore = true)
    Compilation toModelFromNew(NewCompilationDto newCompilationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    void mapToModelFromUpdate(UpdateCompilationDto updateCompilationDto, @MappingTarget Compilation compilation);
}
