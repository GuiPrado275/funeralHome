package com.guilherme.funeralHome.exceptions;

import com.guilherme.funeralHome.services.exceptions.AuthorizationException;
import com.guilherme.funeralHome.services.exceptions.BusinessRuleException;
import com.guilherme.funeralHome.services.exceptions.DataBindingViolationException;
import com.guilherme.funeralHome.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler {

    //vem de "spring.web.error.include-exception" no application.properties
    @Value("${spring.web.error.include-exception}")
    private boolean printStackTrace;

    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT) //argumentos inválidos
    protected ResponseEntity<Object> handleMethodArgumentNotValid( //captura os erros de validação dos DTOs
                                                                   MethodArgumentNotValidException methodArgumentNotValidException, //e devolve um ResponseEntity
                                                                   HttpHeaders headers,
                                                                   HttpStatusCode status,
                                                                   WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                "Erro de validação. Veja o campo 'errors' para os detalhes.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableContent().body(errorResponse);

    } //ex: DTO de atendimento/falecido sem um campo obrigatório (deceasedId, deathDateTime, items, etc.)

    @Override //os demais erros do Spring MVC (JSON malformado, "abc" como id...) usam o mesmo formato ErrorResponse
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        String message;
        if (exception instanceof HttpMessageNotReadableException) {
            message = "Corpo da requisição inválido. Verifique o JSON, as datas (yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss) " +
                    "e os valores permitidos dos campos.";
        } else if (exception instanceof NoResourceFoundException) {
            message = "Rota não encontrada.";
        } else if (exception instanceof HttpRequestMethodNotSupportedException methodNotSupported) {
            message = "Método HTTP não suportado nesta rota: " + methodNotSupported.getMethod() + ".";
        } else if (exception instanceof HttpMediaTypeNotSupportedException) {
            message = "Envie o corpo da requisição em JSON (Content-Type: application/json).";
        } else if (exception instanceof MissingServletRequestParameterException missingParameter) {
            message = "Parâmetro obrigatório ausente: " + missingParameter.getParameterName() + ".";
        } else if (exception instanceof MethodArgumentTypeMismatchException typeMismatch) {
            message = "Valor inválido para '" + typeMismatch.getName() + "': " + typeMismatch.getValue() + ".";
        } else if (body instanceof ProblemDetail problemDetail && problemDetail.getDetail() != null) {
            message = problemDetail.getDetail();
        } else {
            message = exception.getMessage();
        }
        ErrorResponse errorResponse = new ErrorResponse(statusCode.value(), message);
        return ResponseEntity.status(statusCode).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //erro não reconhecido
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            Exception exception, WebRequest request) {
        final String errorMessage = "Erro interno inesperado.";
        log.error(errorMessage, exception);
        return buildErrorResponse(
                exception,
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class) //violações de restrição do banco
    @ResponseStatus(HttpStatus.CONFLICT) //ex: salvar uma entidade que quebra uma restrição unique/FK
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request) {
        //a mensagem do banco (SQL, nomes de restrições, valores) vai só para o log, nunca para o cliente
        log.error("Failed to save entity with integrity problems: "
                + dataIntegrityViolationException.getMostSpecificCause().getMessage(), dataIntegrityViolationException);
        return buildErrorResponse(
                dataIntegrityViolationException,
                "Os dados conflitam com um registro existente (ex: valor que deve ser único já cadastrado).",
                HttpStatus.CONFLICT,
                request);
    }

    @ExceptionHandler(ConstraintViolationException.class) //ex: bean validation falhando fora do corpo da requisição
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Failed to validate element", constraintViolationException);
        String message = constraintViolationException.getConstraintViolations().stream() //só as mensagens legíveis
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" "));
        return buildErrorResponse(
                constraintViolationException,
                message,
                HttpStatus.UNPROCESSABLE_CONTENT,
                request);
    }

    @ExceptionHandler(ObjectNotFoundException.class) //ex: id de atendimento/falecido/produto/plano/usuário que não existe
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(
            ObjectNotFoundException objectNotFoundException,
            WebRequest request) {
        log.error("Failed to find the requested element", objectNotFoundException);
        return buildErrorResponse(
                objectNotFoundException,
                HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(DataBindingViolationException.class) //ex: excluir um usuário que ainda tem atendimentos cadastrados
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(
            DataBindingViolationException dataBindingViolationException,
            WebRequest request) {
        log.error("Failed to save entity with associated data ", dataBindingViolationException);
        return buildErrorResponse(
                dataBindingViolationException,
                HttpStatus.CONFLICT,
                request);
    }

    //ex: sala já reservada, falta de estoque, cremação sem os requisitos legais, limite de dependentes, etc.
    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleBusinessRuleException(
            BusinessRuleException businessRuleException,
            WebRequest request) {
        log.error("Business rule violated ", businessRuleException);
        return buildErrorResponse(
                businessRuleException,
                HttpStatus.CONFLICT,
                request);
    }

    @ExceptionHandler(AuthenticationException.class) //credenciais erradas ou ausentes
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException authenticationException,
            WebRequest request) {
        log.error("Authentication error ", authenticationException);
        return buildErrorResponse(
                authenticationException,
                HttpStatus.UNAUTHORIZED,
                request);
    }

    @ExceptionHandler(AccessDeniedException.class) //nega acesso a perfis sem permissão
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAcessDeniedException(
            AccessDeniedException accessDeniedException,
            WebRequest request) {
        log.error("Authorization error ", accessDeniedException);
        return buildErrorResponse(
                accessDeniedException,
                HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(AuthorizationException.class) //ex: USER tentando ver o atendimento de outra família ou mexer no catálogo
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAuthorizationException(
            AuthorizationException authorizationException,
            WebRequest request) {
        log.error("Authorization error ", authorizationException);
        return buildErrorResponse(
                authorizationException,
                HttpStatus.FORBIDDEN,
                request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        Integer status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(status, "E-mail ou senha inválidos.");
        response.getWriter().append(errorResponse.toJson());
    }

}