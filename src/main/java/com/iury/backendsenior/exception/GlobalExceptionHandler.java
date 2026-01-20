package com.iury.backendsenior.exception;

import com.iury.backendsenior.dto.ErroValidacaoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacaoDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        List<ErroValidacaoDTO.CampoErro> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErroValidacaoDTO.CampoErro(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        ErroValidacaoDTO dto = new ErroValidacaoDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Erro na validação dos campos",
                erros
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}