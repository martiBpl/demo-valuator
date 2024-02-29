package demo.inpost.valuator.product.controller;

import demo.inpost.valuator.common.dto.ErrorDto;
import demo.inpost.valuator.product.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

import static java.lang.String.format;

@ControllerAdvice
public class ProductControllerExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorDto> handleNotFound(HandlerMethodValidationException ex, HttpServletRequest request) {
        String errorMessage = ex.getAllValidationResults().stream().flatMap(it -> it.getResolvableErrors().stream().map(MessageSourceResolvable::getDefaultMessage)).collect(Collectors.joining(". "));
        ErrorDto errorDto = new ErrorDto(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        var message = format("Missing required parameter: %s", ex.getParameterName());
        ErrorDto errorDto = new ErrorDto(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


}
