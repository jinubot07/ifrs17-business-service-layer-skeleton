package com.koreanre.ifrs17.businessservice.core.response;

import com.koreanre.ifrs17.businessservice.api.dto.response.ErrorDetail;
import com.koreanre.ifrs17.businessservice.api.dto.response.ResponseStatus;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardError;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * StandardResponseBuilder 기본 구현체.
 *
 * <p>설계서 5.4 / 5.5 JSON 규격을 그대로 생성한다.</p>
 */
@Component
public class DefaultStandardResponseBuilder implements StandardResponseBuilder {

    /** 기본 Source System (설계서 5.4). */
    public static final String DEFAULT_SOURCE_SYSTEM = "IFRS17";

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    @Override
    public <T> StandardResponse<T> success(ServiceContext context, ServiceMetadata metadata, T result,
                                           long elapsedMs) {
        StandardResponse<T> response = StandardResponse.create();
        response.setRequestId(context.getRequestId());
        response.setTraceId(context.getTraceId());
        response.setServiceId(context.getServiceId());
        response.setServiceVersion(metadata != null ? metadata.getVersion() : context.getServiceVersion());
        response.setSourceSystem(metadata != null ? metadata.getSourceSystem() : DEFAULT_SOURCE_SYSTEM);
        response.setStatus(ResponseStatus.SUCCESS);
        response.setProcessedAt(now());
        response.setElapsedMs(elapsedMs);
        response.setResult(result);
        response.setWarnings(new ArrayList<com.koreanre.ifrs17.businessservice.api.dto.response.Warning>(
                context.getWarnings()));
        return response;
    }

    @Override
    public StandardResponse<Object> error(ServiceContext context, ErrorCode errorCode, String message,
                                          String errorId, List<ErrorDetail> details, long elapsedMs) {
        StandardResponse<Object> response = StandardResponse.create();
        if (context != null) {
            response.setRequestId(context.getRequestId());
            response.setTraceId(context.getTraceId());
            response.setServiceId(context.getServiceId());
            response.setServiceVersion(context.getServiceVersion());
        }
        response.setSourceSystem(DEFAULT_SOURCE_SYSTEM);
        response.setStatus(ResponseStatus.ERROR);
        response.setProcessedAt(now());
        response.setElapsedMs(elapsedMs);

        StandardError error = new StandardError(errorCode.code(), message, errorId);
        if (details != null && !details.isEmpty()) {
            error.setDetails(new ArrayList<ErrorDetail>(details));
        }
        response.setError(error);
        return response;
    }

    private OffsetDateTime now() {
        return OffsetDateTime.now(SERVICE_ZONE).truncatedTo(ChronoUnit.SECONDS);
    }
}
