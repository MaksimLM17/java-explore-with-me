package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {

    private Set<Integer> events;
    private Boolean pinned;

    @Length(min = 1, max = 50)
    private String title;
}
