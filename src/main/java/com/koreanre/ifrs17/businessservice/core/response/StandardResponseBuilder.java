package com.koreanre.ifrs17.businessservice.core.response;

import com.koreanre.ifrs17.businessservice.api.dto.response.ErrorDetail;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;

import java.util.List;

/**
 * 공통 Response Envelope 생성 컴포넌트 (설계서 4.2 - StandardResponseBuilder).
 *
 * <p>필수 인터페이스: {@code success / error}</p>
 */
public interface StandardResponseBuilder {

    /** 표준 Success Response 생성 (설계서 5.4). */
    <T> StandardResponse<T> success(ServiceContext context, ServiceMetadata metadata, T result, long elapsedMs);

    /** 표준 Error Response 생성 (설계서 5.5). */
    StandardResponse<Object> error(ServiceContext context, ErrorCode errorCode, String message,
                                   String errorId, List<ErrorDetail> details, long elapsedMs);
}
