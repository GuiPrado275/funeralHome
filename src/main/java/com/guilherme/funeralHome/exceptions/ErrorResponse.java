package com.guilherme.funeralHome.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final int status;
    private final String message;
    private String stackTrace;
    private List<ValidationError> errors;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(errors)) {
            this.errors = new ArrayList<>(); //se a lista for nula, cria uma nova
        }
        this.errors.add(new ValidationError(field, message));
    }

    public String toJson() { //JSON do erro para respostas escritas fora dos controllers (login inválido, 401)
        return "{\"status\": " + getStatus() + ", " +
                "\"message\": \"" + getMessage() + "\"}";
    }
}