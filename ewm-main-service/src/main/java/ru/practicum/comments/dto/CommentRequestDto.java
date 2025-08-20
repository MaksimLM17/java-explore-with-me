package ru.practicum.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    @NotBlank(message = "Текст комментария не может быть пустым или состоять из пробелов!")
    @Size(min = 1, max = 2000, message = "Максимальное количество символов - 2000!")
    private String text;
}
