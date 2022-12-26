package com.example.groupboardservice.exception;

import com.example.groupboardservice.exception.enums.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomExceptionEntity> exceptionHandler(
        HttpServletRequest request, final CustomException ex
    ) {
        log.error(ex.toString());
        return ResponseEntity
            .status(ex.getException().getStatus())
            .body(CustomExceptionEntity.builder()
                .result(ex.getException().getResult())
                .message(ex.getException().getMessage())
                .build());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<CustomExceptionEntity> exceptionHandler(
        HttpServletRequest request, final RuntimeException ex
    ) {
        log.error(ex.toString());
        return ResponseEntity
            .status(ExceptionEnum.RUNTIME_EXCEPTION.getStatus())
            .body(CustomExceptionEntity.builder()
                .result(ExceptionEnum.RUNTIME_EXCEPTION.getResult())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<CustomExceptionEntity> exceptionHandler(
        HttpServletRequest request, final AccessDeniedException ex
    ) {
        log.error(ex.toString());
        return ResponseEntity
            .status(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getStatus())
            .body(CustomExceptionEntity.builder()
                .result(ExceptionEnum.INTERNAL_SERVER_ERROR.getResult())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<CustomExceptionEntity> exceptionHandler(
        HttpServletRequest request, final Exception ex
    ) {
        log.error(ex.toString());
        return ResponseEntity
            .status(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getStatus())
            .body(CustomExceptionEntity.builder()
                .result(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getResult())
                .message(ex.getMessage())
                .build());
    }
}
