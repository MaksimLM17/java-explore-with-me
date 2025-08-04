package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalHandler {

    private final static String BAD_VALID = "Ошибка валидации";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleValidationExceptionNotFound(NotFoundException e) {
        return ErrorResponse.builder().error(BAD_VALID).details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptionBadRequest(BadRequestException e) {
        return ErrorResponse.builder().error(BAD_VALID).details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationDuplicateEmail(UniqueConflictException e) {
        return ErrorResponse.builder().error(BAD_VALID).details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationMethodExceptions(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        });
        log.error("Ошибка валидации: {}", errorMessage);
        return ErrorResponse.builder().error(BAD_VALID).details(errorMessage.toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintException(ConstraintViolationException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getConstraintViolations().forEach(violation -> {
            errorMessage.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; ");
        });
        log.error("Ошибка валидации: {}", errorMessage);
        return ErrorResponse.builder().error(BAD_VALID).details(errorMessage.toString()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String parameterName = e.getName();
        String parameterValue = String.valueOf(e.getValue());
        String errorMessage = String.format("Передано некорректное значение параметра %s: %s",
                parameterName, parameterValue);
        log.error(errorMessage);
        return ErrorResponse.builder().error(errorMessage).details(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHandlerMethodValidation(HandlerMethodValidationException e) {
        StringBuilder details = new StringBuilder();
        e.getAllValidationResults().forEach(result -> {
            String name = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> {
                details.append(name)
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            });
        });
        log.error("Ошибка валидации параметров: {}", details);
        return ErrorResponse.builder()
                .error(BAD_VALID)
                .details(details.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherException(Exception e) {
        String exceptionClassName = e.getClass().getSimpleName();
        log.error("{}: {}", exceptionClassName, e.getMessage());

        return ErrorResponse.builder()
                .error(exceptionClassName)
                .details(e.getMessage())
                .build();
    }
}
