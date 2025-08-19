package ru.practicum.comments.model;

public enum CommentStatus {
    PENDING,    // На модерации (по умолчанию для новых комментариев)
    PUBLISHED,  // Опубликован (прошел модерацию)
    REJECTED,   // Отклонен модератором
    EDITED      // Отредактирован (требует повторной модерации)
}
