package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {

    private String appName;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stat-server.url}") String serverUrl,
                       @Value("${app.name}") String appName) {
        super(createRestClient(serverUrl));
        this.appName = appName;
    }

    private static RestClient createRestClient(String serverUrl) {
        return RestClient.builder()
                .baseUrl(serverUrl)
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<Object> create(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        return post("/hit", createDto(uri, ip));
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end,
                                           List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris != null ? String.join(",", uris) : "",
                "unique", unique != null ? unique : false
        );
        return get("/stats", parameters);
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
