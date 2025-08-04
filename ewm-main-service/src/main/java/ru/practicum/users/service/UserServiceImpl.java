package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.UniqueConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserShortDto userShortDto) {
        existEmail(userShortDto.getEmail());
        User user = userRepository.save(userMapper.mapToModelFromShortDto(userShortDto));
        log.info("Пользователь сохранён с данными: {}", userShortDto);
        return userMapper.mapToUserDtoFromModel(user);
    }

    @Override
    public List<UserDto> findByParameters(List<Integer> ids, Integer from, Integer size) {
        int pageNumber = (int) Math.floor((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<User> page = ids == null
                ? userRepository.findAll(pageable)
                : userRepository.findByIdIn(ids, pageable);

        List<UserDto> result = page.getContent().stream()
                .map(userMapper::mapToUserDtoFromModel)
                .toList();
        log.info("Отправлен список пользователей размером: {}", result.size());
        return result;
    }

    @Override
    public void delete(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь с id = {}, не найден!", userId);
            throw new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId));
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {}, удалён!", userId);
    }

    private void existEmail(String email) {
        boolean hasEmail = userRepository.existsByEmail(email);
        if (hasEmail) {
            log.error("Пользователь с данным email = {}, уже существует!", email);
            throw new UniqueConflictException("Пользователь с данным email уже существует");
        }
    }
}
