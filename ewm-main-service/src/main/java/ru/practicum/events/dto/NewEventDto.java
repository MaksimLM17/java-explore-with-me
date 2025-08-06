package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.location.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Краткое описание события не может быть пустым или содержать только пробелы!")
    @Size(min = 20, max = 2000, message = "Краткое описание события не может содержать больше 2000 символов и меньше 20!")
    private String annotation;
    
    @NotNull(message = "Id категории должен быть указан!")
    private Integer category;

    @NotBlank(message = "Описание события не может быть пустым или содержать только пробелы!")
    @Size(min = 20, max = 7000, message = "Описание события не может содержать больше 7000 символов и меньше 20!")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Дата события должна быть в будущем")
    private LocalDateTime eventDate;

    @NotNull(message = "Координаты места проведения должны быть указаны!")
    private Location location;
    private boolean paid = false;
    private Integer participantLimit = 0;
    private boolean requestModeration = true;

    @NotBlank(message = "Заголовок должен быть указан!")
    @Size(min = 3, max = 120, message = "Заголовок не может содержать больше 120 и меньше 3-х символов!")
    private String title;

}
