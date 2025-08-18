package ru.practicum.events.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPublishedEvents {

    private String text;

    private List<Integer> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable = false;

    @Pattern(regexp = "EVENT_DATE|VIEWS", message = "Некорректный параметр сортировки")
    private String sort = "EVENT_DATE";

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;

    @AssertTrue(message = "Дата начала не может быть позже даты окончания")
    public boolean isValidDateRange() {
        return rangeStart == null || rangeEnd == null || !rangeStart.isAfter(rangeEnd);
    }
}
