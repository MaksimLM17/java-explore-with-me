package ru.practicum.events.location;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Широта должна быть указана!")
    private Float lat;
    @NotNull(message = "Долгота должна быть указана!")
    private Float lon;
}
