package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 내부 시스템 오류 (HTTP 500 / BS-SYS-500) - 설계서 4.5.
 *
 * <p>외부 응답에는 상세를 숨기고 Error ID 만 노출한다(설계서 6.4).</p>
 */
public class SystemException extends BusinessServiceException {

    private static final long serialVersionUID = 1L;

    public SystemException(String message) {
        super(ErrorCode.BS_SYS_500, message);
    }

    public SystemException(String message, Throwable cause) {
        super(ErrorCode.BS_SYS_500, message, cause);
    }
}
