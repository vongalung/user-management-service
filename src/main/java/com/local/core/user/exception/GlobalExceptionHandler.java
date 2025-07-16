package com.local.core.user.exception;

import static org.springframework.http.HttpStatus.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.net.URI;
import java.util.stream.Collectors;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail badRequest(HttpServletRequest req, BadRequestException ex) {
        return handleBaseApplicationException(req, BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail badRequest(HttpServletRequest req, ConflictException ex) {
        return handleBaseApplicationException(req, CONFLICT, ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail unauthorized(HttpServletRequest req, UnauthorizedException ex) {
        return handleBaseApplicationException(req, UNAUTHORIZED, ex);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ProblemDetail internalServerError(HttpServletRequest req,
                                             InternalServerErrorException ex) {
        return handleBaseApplicationException(req, INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgNotValid(HttpServletRequest req,
                                           MethodArgumentNotValidException ex) {
        return handleBaseApplicationException(req, BAD_REQUEST, ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail missingRequestParam(HttpServletRequest req,
                                             MissingServletRequestParameterException ex) {
        return handleBaseApplicationException(req, BAD_REQUEST, ex);
    }

    ProblemDetail handleBaseApplicationException(HttpServletRequest req,
                                                 HttpStatus status, Exception ex) {
        String message = generateExceptionMessage(ex);
        log.error(message, ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status, message);
        problemDetail.setTitle(generateError(status, ex));
        problemDetail.setType(URI.create(req.getRequestURI()));
        return problemDetail;
    }

    String generateError(HttpStatus status, Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            return status.getReasonPhrase();
        }
        if (ex instanceof MissingServletRequestParameterException) {
            return status.getReasonPhrase();
        }
        return ex.getClass().getSimpleName();
    }

    String generateExceptionMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(this::createFieldErrorMessage)
                .collect(Collectors.joining("\n"));
    }

    String generateExceptionMessage(MissingServletRequestParameterException ex) {
        return "Request parameter `" + ex.getParameterName() + "` is required.";
    }

    String createFieldErrorMessage(FieldError e) {
        return e.getField() + " " + e.getDefaultMessage();
    }

    String generateExceptionMessage(BaseApplicationException ex) {
        return ex.getMessage();
    }

    String generateExceptionMessage(Exception ex) {
        if (ex instanceof BaseApplicationException exception) {
            return generateExceptionMessage(exception);
        }
        if (ex instanceof MissingServletRequestParameterException exception) {
            return generateExceptionMessage(exception);
        }
        if (ex instanceof MethodArgumentNotValidException exception) {
            return generateExceptionMessage(exception);
        }
        return ex.getMessage();
    }
}
