package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 서비스/버전 없음 또는 비활성 (HTTP 404 / BS-SVC-404) - 설계서 4.5 / 8.4.
 */
public class ServiceNotFoundException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public ServiceNotFoundException(String message) {
        super(ErrorCode.BS_SVC_404, message);
    }
}
