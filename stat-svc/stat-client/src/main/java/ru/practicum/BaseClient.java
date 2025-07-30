package ru.practicum;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BaseClient {

    protected final RestClient restClient;

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        try {
            RestClient.RequestBodySpec requestSpec = restClient.method(method)
                    .uri(uriBuilder -> {
                        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
                        if (parameters != null) {
                            parameters.forEach(builder::queryParam);
                        }
                        return builder.build().toUri();
                    })
                    .headers(headers -> {
                        headers.setAll(defaultHeaders().toSingleValueMap());
                    });

            if (body != null) {
                requestSpec.body(body);
            }

            ResponseEntity<Object> responseEntity = requestSpec.retrieve()
                    .toEntity(Object.class);

            return prepareResponse(responseEntity);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
