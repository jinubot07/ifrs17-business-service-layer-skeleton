package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * Timeout (HTTP 504 / BS-SYS-504) - 설계서 4.5 / 3.3(기본 30초).
 */
public class ServiceTimeoutException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public ServiceTimeoutException(String message) {
        super(ErrorCode.BS_SYS_504, message);
    }
}
