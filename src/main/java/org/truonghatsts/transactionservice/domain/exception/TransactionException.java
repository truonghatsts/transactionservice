package org.truonghatsts.transactionservice.domain.exception;

import lombok.Data;

@Data
public class TransactionException extends Exception {

    private int code;
    private String message;

    public TransactionException(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
}
