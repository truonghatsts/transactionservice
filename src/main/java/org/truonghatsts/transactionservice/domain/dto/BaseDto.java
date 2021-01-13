package org.truonghatsts.transactionservice.domain.dto;

import lombok.Data;

import static org.truonghatsts.transactionservice.constants.CodeMessage.PROCESSING_SUCCESSFULLY_CODE;
import static org.truonghatsts.transactionservice.constants.CodeMessage.PROCESSING_SUCCESSFULLY_MESSAGE;

@Data
public class BaseDto<T> {

    private int code;
    private String message;
    private T data;

    // This constructor is used for failed process
    public BaseDto(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // This constructor is used for successful process
    public BaseDto(T data) {
        this.code = PROCESSING_SUCCESSFULLY_CODE;
        this.message = PROCESSING_SUCCESSFULLY_MESSAGE;
        this.data = data;
    }

    // This constructor is used for successful process
    public BaseDto() {
        this.code = PROCESSING_SUCCESSFULLY_CODE;
        this.message = PROCESSING_SUCCESSFULLY_MESSAGE;
    }
}
