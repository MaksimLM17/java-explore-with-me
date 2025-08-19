package ru.practicum.events.location;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    @NotNull(message = "Широта должна быть указана!")
    private float lat;
    @NotNull(message = "Долгота должна быть указана!")
    private float lon;
}
