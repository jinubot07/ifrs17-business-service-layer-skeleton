package com.koreanre.ifrs17.businessservice.persistence.model;

import java.time.LocalDateTime;

/**
 * BS_CALL_LOG 테이블 모델 (설계서 6.3 감사로그 필수항목 / 7.3 핵심 DDL).
 *
 * <p><b>[Skeleton]</b> 순수 Java POJO. 현재 감사로그는 DB 대신 콘솔로 출력되며
 * ({@code ConsoleAuditLogger}), 본 모델은 DB 연동 단계의 저장 규격을 미리 고정하기 위한 것이다.</p>
 *
 * <p>설계서 6.4에 따라 Request 원문은 저장하지 않고 parameterHash 만 보관한다.</p>
 */
public class BsCallLog {

    private String requestId;
    private String traceId;
    private String serviceId;
    private String serviceVersion;
    private String clientId;
    private String userId;
    private String departmentCode;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private Integer elapsedMs;
    private String statusCode;
    private Integer httpStatus;
    private String errorCode;
    private String errorId;
    private String parameterHash;
    private Integer resultCount;
    private String remoteIp;
    private String serverInstance;

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(Integer elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getParameterHash() {
        return parameterHash;
    }

    public void setParameterHash(String parameterHash) {
        this.parameterHash = parameterHash;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(String serverInstance) {
        this.serverInstance = serverInstance;
    }
}
