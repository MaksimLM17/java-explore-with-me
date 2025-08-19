package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public User mapToModel(UserDto userDto);

    public UserDto mapToUserDtoFromModel(User user);

    @Mapping(target = "id", ignore = true)
    public User mapToModelFromShortDto(UserShortDto userShortDto);
}
