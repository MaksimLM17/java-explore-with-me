package ru.practicum.events.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.RequestState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusUpdateRequest {

    @NotNull(message = "Список ID запросов обязателен")
    @NotEmpty(message = "Список ID запросов не может быть пустым")
    private List<Integer> requestIds;

    @NotNull(message = "Обязательное поле")
    private RequestState status;
}
