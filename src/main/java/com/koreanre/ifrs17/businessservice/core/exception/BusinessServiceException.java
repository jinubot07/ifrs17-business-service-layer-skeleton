package com.koreanre.ifrs17.businessservice.core.exception;

import com.koreanre.ifrs17.businessservice.api.dto.response.ErrorDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Business Service Layer 표준 예외 최상위 클래스 (설계서 4.5 예외 처리 기준).
 *
 * <p>모든 하위 예외는 {@link ErrorCode} 를 보유하며, Executor 가 표준 Error Response 로 변환한다.</p>
 */
public class BusinessServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final List<ErrorDetail> details = new ArrayList<ErrorDetail>();

    public BusinessServiceException(ErrorCode errorCode) {
        this(errorCode, errorCode.defaultMessage(), null);
    }

    public BusinessServiceException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public BusinessServiceException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }

    public BusinessServiceException addDetail(String field, String message) {
        this.details.add(new ErrorDetail(field, message));
        return this;
    }
}
