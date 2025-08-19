package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Integer id;

    @NotBlank(message = "Название категории не может быть пустым или состоять из пробелов!")
    @Size(min = 1, max = 50, message = "Название категории не может превышать 50 символов")
    private String name;
}
