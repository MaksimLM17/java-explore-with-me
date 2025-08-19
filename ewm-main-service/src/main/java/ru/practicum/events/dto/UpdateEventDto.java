package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.location.LocationDto;
import ru.practicum.events.state.StateAction;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {

    @Size(min = 20, max = 2000, message = "Краткое описание события не может содержать больше 2000 символов и меньше 20!")
    private String annotation;

    private Integer category;

    @Size(min = 20, max = 7000, message = "Описание события не может содержать больше 7000 символов и меньше 20!")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Дата события должна быть в будущем")
    private LocalDateTime eventDate;

    private LocationDto locationDto;
    private Boolean paid;

    @PositiveOrZero(message = "Количество участников не может быть меньше нуля! Ноль-без ограничения по количеству!")
    private Integer participantLimit;
    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Заголовок не может содержать больше 120 и меньше 3-х символов!")
    private String title;
}
