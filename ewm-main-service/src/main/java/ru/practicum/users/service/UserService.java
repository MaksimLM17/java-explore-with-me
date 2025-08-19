package ru.practicum.users.service;

import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;

import java.util.List;

public interface UserService {

    UserDto create(UserShortDto userShortDto);

    List<UserDto> findByParameters(List<Integer> ids, Integer from, Integer size);

    void delete(Integer userId);
}
