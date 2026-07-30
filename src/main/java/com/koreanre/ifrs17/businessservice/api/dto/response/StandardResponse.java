package com.koreanre.ifrs17.businessservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공통 Response Envelope (설계서 5.4 표준 Success Response / 5.5 표준 Error Response).
 *
 * <p>성공/오류 모두 동일한 Envelope 을 사용하며, 해당되지 않는 필드는 직렬화에서 제외된다.</p>
 *
 * @param <T> result 타입
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponse<T> {

    private String requestId;
    private String traceId;
    private String serviceId;
    private String serviceVersion;
    private String sourceSystem;
    private ResponseStatus status;
    private OffsetDateTime processedAt;
    private Long elapsedMs;

    /** 성공 응답의 업무 결과. */
    private T result;

    /** 경고(예: BS-DATA-000 결과 없음). 성공 응답에서만 사용한다. */
    private List<Warning> warnings;

    /** 오류 응답 상세 (설계서 5.5). */
    private StandardError error;

    public static <T> StandardResponse<T> create() {
        return new StandardResponse<T>();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(OffsetDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Long getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(Long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Warning> warnings) {
        this.warnings = (warnings == null) ? new ArrayList<Warning>() : warnings;
    }

    public StandardError getError() {
        return error;
    }

    public void setError(StandardError error) {
        this.error = error;
    }
}
