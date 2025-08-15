package ru.practicum.compilations.dto;


import jakarta.validation.constraints.NotBlank;
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
    private String title;
}
