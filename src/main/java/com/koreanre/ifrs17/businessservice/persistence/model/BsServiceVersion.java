package com.koreanre.ifrs17.businessservice.persistence.model;

import java.time.LocalDateTime;

/**
 * BS_SERVICE_VERSION 테이블 모델 (설계서 7.3 핵심 DDL).
 *
 * <p><b>[Skeleton]</b> 순수 Java POJO. DB 연결/ORM 애노테이션 없음.</p>
 */
public class BsServiceVersion {

    private String serviceId;
    private String version;
    /** Spring Bean 명 (설계서 8.3 - Handler 검증 기준). */
    private String implementationBean;
    private int timeoutMs = 30000;
    private String requestSchema;
    private String responseSchema;
    /** ACTIVE / INACTIVE 등 (설계서 7.3 status_code). */
    private String statusCode;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImplementationBean() {
        return implementationBean;
    }

    public void setImplementationBean(String implementationBean) {
        this.implementationBean = implementationBean;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public String getRequestSchema() {
        return requestSchema;
    }

    public void setRequestSchema(String requestSchema) {
        this.requestSchema = requestSchema;
    }

    public String getResponseSchema() {
        return responseSchema;
    }

    public void setResponseSchema(String responseSchema) {
        this.responseSchema = responseSchema;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}
