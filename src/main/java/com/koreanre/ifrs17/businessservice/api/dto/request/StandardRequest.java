package com.koreanre.ifrs17.businessservice.api.dto.request;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 표준 Request DTO (설계서 5.3 표준 Request).
 *
 * <pre>
 * {
 *   "serviceVersion": "1.0",
 *   "parameters": { "closingYearMonth": "2026-06", "accountingBasis": "IFRS17" },
 *   "options": { "locale": "ko-KR", "includeDetails": false }
 * }
 * </pre>
 */
public class StandardRequest {

    /** 서비스 Minor Version. 미입력 시 Catalog 의 기본(활성) 버전을 사용한다. */
    private String serviceVersion;

    /** 업무 파라미터. 서비스별 Request DTO 로 바인딩된다. */
    private Map<String, Object> parameters = new LinkedHashMap<String, Object>();

    /** 호출 옵션(locale, includeDetails 등). */
    private RequestOptions options = new RequestOptions();

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = (parameters == null) ? new LinkedHashMap<String, Object>() : parameters;
    }

    public RequestOptions getOptions() {
        return options;
    }

    public void setOptions(RequestOptions options) {
        this.options = (options == null) ? new RequestOptions() : options;
    }
}
