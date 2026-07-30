package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 입력 검증 오류 (HTTP 400 / BS-VAL-001) - 설계서 4.5.
 */
public class ValidationException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(ErrorCode.BS_VAL_001, message);
    }

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static ValidationException of(String field, String message) {
        ValidationException ex = new ValidationException(message);
        ex.addDetail(field, message);
        return ex;
    }
}
