package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;
    private final StatMapper mapper;

    @Override
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        log.debug("Получен запрос на создание хита: {}", endpointHitDto);
        EndpointHit endpointHit = statisticRepository.save(mapper.mapToModelEndpoint(endpointHitDto));
        log.info("Хит сохранен в БД с данными: {}", endpointHit);
        return mapper.mapToDtoEndpoint(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.debug("Получен запрос на выгрузку статистики с данными:" +
                " start =  {}, end =  {}, uris.size() = {}, unique =  {}", start, end, uris.size(), unique);
        checkTime(start, end);
        if (unique) {
            log.info("Отправлен список данных с уникальными ip!");
            return statisticRepository.findAllUriUnique(start, end, uris).stream()
                    .map(mapper::mapToDtoView)
                    .toList();
        } else {
            log.info("Отправлен список данных без учета уникальности ip!");
            return statisticRepository.findAllUri(start, end, uris).stream()
                    .map(mapper::mapToDtoView)
                    .toList();
        }
    }

    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start == end) {
            log.error("Значение start = {}, после значения end = {},  или одинаково!", start, end);
            throw new BadRequestException("Некорректные временные рамки! Значение start после значения end или одинаково!");
        }
    }
}
