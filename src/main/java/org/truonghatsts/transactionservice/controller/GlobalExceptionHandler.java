package org.truonghatsts.transactionservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.truonghatsts.transactionservice.domain.dto.BaseDto;
import org.truonghatsts.transactionservice.domain.exception.TransactionException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TransactionException.class)
    public final ResponseEntity<BaseDto> handleTransactionException(TransactionException ex) {
        log.error("handleTransactionException : {}", ex.getMessage(), ex);
        BaseDto error = new BaseDto();
        error.setCode(ex.getCode());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error("handleAllExceptions : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong, please contact administrator");
    }
}