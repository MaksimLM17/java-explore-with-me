package ru.practicum.users.dto;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private List<Integer> ids;

    @PositiveOrZero(message = "Значение from должно быть больше либо равно нулю")
    private Integer from = 0;

    @Positive(message = "Значение size должно быть больше нуля")
    private Integer size = 10;
}
