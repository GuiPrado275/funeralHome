package com.guilherme.funeralHome.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//lançada quando uma regra de negócio é violada (sala já reservada, falta de estoque,
//cremação sem os requisitos legais, dependentes demais no plano, etc.)
@ResponseStatus(value = HttpStatus.CONFLICT)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
