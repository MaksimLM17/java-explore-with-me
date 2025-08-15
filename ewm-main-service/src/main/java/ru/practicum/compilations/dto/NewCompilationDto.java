package ru.practicum.compilations.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private Set<Integer> events;
    private boolean pinned = false;

    @NotBlank(message = "Заголовок должен быть указан")
    @Size(min = 0, max = 50, message = "Заголовок превышает 50 символов!")
    private String title;
}
