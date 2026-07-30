package com.koreanre.ifrs17.businessservice.console;

import java.util.ArrayList;
import java.util.List;

/**
 * 서비스 명세 등록/수정 입력 폼 (설계서 8.3 서비스 명세 관리 - CON-01).
 *
 * <p>설계서 8.3 표의 입력 항목을 그대로 반영한다.</p>
 */
public class ServiceSpecificationForm {

    /** Service ID (필수). 대문자·점 구분, 중복 금지, Handler ID와 일치. */
    private String serviceId;

    /** 서비스명 (필수, 100자 이내). */
    private String serviceName;

    /** 버전 (필수). 동일 Service ID 내 중복 금지. */
    private String version;

    /** Spring Bean명 (필수). WAS에 존재하고 공통 Interface 구현. */
    private String implementationBean;

    /** 서비스 설명 (필수). */
    private String description;

    /** 요청 JSON 명세 (필수). */
    private String requestSchema;

    /** 응답 JSON 명세 (필수). */
    private String responseSchema;

    /** 허용 역할 (필수). */
    private List<String> requiredRoles = new ArrayList<String>();

    /** 사용 여부 (필수). 미사용 시 외부 호출 차단. */
    private boolean useYn = true;

    /** 도메인 코드 (선택). */
    private String domainCode;

    /** Timeout(ms). 기본 30,000 (설계서 9장). */
    private int timeoutMs = 30000;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(List<String> requiredRoles) {
        this.requiredRoles = (requiredRoles == null) ? new ArrayList<String>() : requiredRoles;
    }

    public boolean isUseYn() {
        return useYn;
    }

    public void setUseYn(boolean useYn) {
        this.useYn = useYn;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
