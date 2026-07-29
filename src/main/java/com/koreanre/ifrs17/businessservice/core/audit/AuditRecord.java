package com.koreanre.ifrs17.businessservice.core.audit;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;

import java.time.LocalDateTime;

/**
 * 감사로그 레코드 (설계서 6.3 감사로그 필수항목 / 7.3 bs_call_log).
 *
 * <p>Skeleton 단계에서는 DB 저장 없이 콘솔로만 출력한다.</p>
 */
public class AuditRecord {

    /* 추적 */
    private String requestId;
    private String traceId;
    private String parentRequestId;

    /* 호출자 */
    private String clientId;
    private String userId;
    private String departmentCode;
    private String roles;

    /* 서비스 */
    private String serviceId;
    private String serviceVersion;
    private String sourceSystem;

    /* 시간 */
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private Long elapsedMs;

    /* 결과 */
    private String status;
    private Integer httpStatus;
    private String errorCode;
    private String errorId;
    private Integer resultCount;

    /* 보안 */
    private String remoteIp;
    private String authType;
    private String authorizationResult;

    /* 데이터 */
    private String parameterHash;
    private boolean sensitiveAccessFlag;

    /* 운영 */
    private String serverInstance;
    private String applicationVersion;

    public static AuditRecord from(ServiceContext context) {
        AuditRecord record = new AuditRecord();
        record.requestId = context.getRequestId();
        record.traceId = context.getTraceId();
        record.clientId = context.getClientId();
        record.userId = context.getUserId();
        record.departmentCode = context.getDepartmentCode();
        record.roles = String.valueOf(context.getRoles());
        record.serviceId = context.getServiceId();
        record.serviceVersion = context.getServiceVersion();
        record.requestedAt = context.getRequestedAt();
        record.remoteIp = context.getRemoteIp();
        record.authType = context.getAuthType();
        return record;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getParentRequestId() {
        return parentRequestId;
    }

    public void setParentRequestId(String parentRequestId) {
        this.parentRequestId = parentRequestId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getRoles() {
        return roles;
    }

    public String getServiceId() {
        return serviceId;
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

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(Long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public String getAuthType() {
        return authType;
    }

    public String getAuthorizationResult() {
        return authorizationResult;
    }

    public void setAuthorizationResult(String authorizationResult) {
        this.authorizationResult = authorizationResult;
    }

    public String getParameterHash() {
        return parameterHash;
    }

    public void setParameterHash(String parameterHash) {
        this.parameterHash = parameterHash;
    }

    public boolean isSensitiveAccessFlag() {
        return sensitiveAccessFlag;
    }

    public void setSensitiveAccessFlag(boolean sensitiveAccessFlag) {
        this.sensitiveAccessFlag = sensitiveAccessFlag;
    }

    public String getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(String serverInstance) {
        this.serverInstance = serverInstance;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
}
