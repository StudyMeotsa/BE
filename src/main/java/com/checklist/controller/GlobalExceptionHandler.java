package com.checklist.controller; 

import com.checklist.exception.DuplicateSubmissionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하고 일관된 HTTP 응답을 반환하는 핸들러 클래스입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * NoSuchElementException (대부분 리소스를 찾지 못했을 때 발생)을 처리합니다.
     * HTTP 404 Not Found 응답을 반환합니다.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        // 

    [Image of 404 Not Found HTTP status code]

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * DuplicateSubmissionException (중복 제출 등 비즈니스 규칙 위반)을 처리합니다.
     * HTTP 409 Conflict 응답을 반환합니다.
     */
    @ExceptionHandler(DuplicateSubmissionException.class)
    public ResponseEntity<String> handleDuplicateSubmissionException(DuplicateSubmissionException ex) {
        // 
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    
    // 필요하다면, 다른 일반적인 예외(예: Validation 예외) 처리를 여기에 추가할 수 있습니다.
}