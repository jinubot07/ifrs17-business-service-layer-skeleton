package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 기간계(Legacy) Service 처리 오류 (HTTP 500 / BS-LEG-500) - 설계서 부록 A.
 */
public class LegacyServiceException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public LegacyServiceException(String message) {
        super(ErrorCode.BS_LEG_500, message);
    }

    public LegacyServiceException(String message, Throwable cause) {
        super(ErrorCode.BS_LEG_500, message, cause);
    }
}
