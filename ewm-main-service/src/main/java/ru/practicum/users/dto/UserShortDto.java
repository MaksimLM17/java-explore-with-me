package ru.practicum.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    @NotBlank(message = "Имя пользователя не может быть пустым или состоять из пробелов")
    @Size(max = 250, min = 2, message = "Имя пользователя не может быть меньше 2 и больше 250 символов!")
    private String name;

    @Email(message = "Некорректный формат емейл")
    @Size(max = 254, min = 6, message = "Длина емейла не может быть меньше 6 и больше 254 символов!")
    @NotBlank(message = "Емейл не может быть пустым или состоять из пробелов")
    private String email;
}
