package com.koreanre.ifrs17.businessservice.core.context;

import com.koreanre.ifrs17.businessservice.api.dto.response.Warning;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 요청 단위 신원/추적 객체 (설계서 4.4 표준 인터페이스 예시).
 *
 * <p>설계서 예시 필드에 더해, Skeleton 처리 흐름에서 필요한 최소 항목
 * (serviceId, serviceVersion, remoteIp, authType, warnings)을 함께 보관한다.</p>
 */
public class ServiceContext {

    private String requestId;
    private String traceId;
    private String clientId;
    private String userId;
    private String departmentCode;
    private Set<String> roles = new LinkedHashSet<String>();
    private LocalDateTime requestedAt;

    /* --- 추적/감사를 위한 부가 항목 (설계서 6.3 감사로그 필수항목) --- */
    private String serviceId;
    private String serviceVersion;
    private String remoteIp;
    private String authType;
    private String locale = "ko-KR";

    /** Handler 가 남긴 경고. Executor 가 표준 Response 의 warnings 로 옮긴다. */
    private final List<Warning> warnings = new ArrayList<Warning>();

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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = (roles == null) ? new LinkedHashSet<String>() : roles;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
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

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    /** 업무 처리 중 발생한 경고를 등록한다(예: BS-DATA-000). */
    public void addWarning(String code, String message) {
        this.warnings.add(new Warning(code, message));
    }

    @Override
    public String toString() {
        return "ServiceContext{requestId='" + requestId + "', traceId='" + traceId
                + "', clientId='" + clientId + "', userId='" + userId
                + "', departmentCode='" + departmentCode + "', roles=" + roles
                + ", serviceId='" + serviceId + "', serviceVersion='" + serviceVersion + "'}";
    }
}
