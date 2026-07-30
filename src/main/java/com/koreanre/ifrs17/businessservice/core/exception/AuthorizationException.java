package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 권한 없음 (HTTP 403 / BS-AUTH-003) - 설계서 4.5 / 6.2.
 */
public class AuthorizationException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public AuthorizationException(String message) {
        super(ErrorCode.BS_AUTH_003, message);
    }
}
