package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String appName;
    private final String serverUrl;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stat-server.url}") String serverUrl,
                       @Value("${app.name}") String appName,
                       RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = serverUrl;
        this.appName = appName;
        log.info("StatsClient initialized with URL: {}", serverUrl);
    }

    public void create(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        post("/hit", createDto(uri, ip));
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end,
                                           List<String> uris, Boolean unique) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start.format(formatter));
        params.put("end", end.format(formatter));
        params.put("uris", uris != null ? String.join(",", uris) : "");
        params.put("unique", unique != null ? unique : false);

        return get("/stats", params);
    }

    private ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeRequest(HttpMethod.GET, path, parameters, null);
    }

    private ResponseEntity<Object> post(String path, Object body) {
        return makeRequest(HttpMethod.POST, path, null, body);
    }

    private ResponseEntity<Object> makeRequest(HttpMethod method, String path,
                                               Map<String, Object> parameters, Object body) {
        String url = serverUrl + path;
        log.info("Отправка {} запроса по url: {} with params: {}", method, url, parameters);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            if (parameters != null) {
                parameters.forEach(builder::queryParam);
            }

            HttpEntity<Object> entity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(
                    builder.build().encode().toUri(),
                    method,
                    entity,
                    Object.class
            );
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private EndpointHitDto createDto(String uri, String ip) {
        return EndpointHitDto.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
    }
}