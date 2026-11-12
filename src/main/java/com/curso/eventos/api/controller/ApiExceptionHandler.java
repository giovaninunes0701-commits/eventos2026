package com.curso.eventos.api.controller;

import com.curso.eventos.application.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private ResponseEntity<ApiError> erro(HttpStatus status,String message,HttpServletRequest request,Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(),status.value(),status.getReasonPhrase(),message,request.getRequestURI(),fields));
    }
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> naoEncontrado(RecursoNaoEncontradoException ex,HttpServletRequest request) { return erro(HttpStatus.NOT_FOUND,ex.getMessage(),request,Map.of()); }
    @ExceptionHandler({RecursoDuplicadoException.class,RecursoEmUsoException.class,DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> duplicado(Exception ex,HttpServletRequest request) { return erro(HttpStatus.CONFLICT,"Recurso em conflito com dado existente",request,Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacao(MethodArgumentNotValidException ex,HttpServletRequest request) {
        Map<String,String> fields=new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(),e.getDefaultMessage()));
        return erro(HttpStatus.BAD_REQUEST,"Um ou mais campos são inválidos",request,fields);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,IllegalArgumentException.class})
    public ResponseEntity<ApiError> entrada(Exception ex,HttpServletRequest request) { return erro(HttpStatus.BAD_REQUEST,"Corpo JSON ou valores inválidos",request,Map.of()); }
}
