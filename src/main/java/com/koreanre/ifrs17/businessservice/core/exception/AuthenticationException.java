package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 인증 실패 (HTTP 401 / BS-AUTH-001, BS-AUTH-002) - 설계서 4.5 / 6.1.
 */
public class AuthenticationException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(ErrorCode.BS_AUTH_001, message);
    }

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
