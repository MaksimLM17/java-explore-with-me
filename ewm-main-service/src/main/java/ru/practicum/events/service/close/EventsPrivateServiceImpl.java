package ru.practicum.events.service.close;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.InvalidDateTimeException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventsPrivateServiceImpl implements EventsPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    public EventDto create(NewEventDto newEventDto, Integer userId) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = %d, не найдена!".formatted(newEventDto.getCategory())));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Начало события не раньше чем через два часа с момента создания!");
            throw new InvalidDateTimeException("Начало события должно быть не раньше чем через два часа с момента создания!");
        }

        Event event = eventMapper.mapToModel(newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        EventDto eventDto = eventMapper.mapToDto(eventRepository.save(event));
        log.info("Событие создано: {}", eventDto);

        return eventDto;
    }

    @Override
    public List<EventDto> getAllEventsUser(Integer userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = %d, не найден!".formatted(userId)));

        int pageNumber = (int) Math.floor((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(eventMapper::mapToDto)
                .toList();
    }
}
