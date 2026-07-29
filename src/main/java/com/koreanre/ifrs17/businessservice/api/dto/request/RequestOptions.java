package com.koreanre.ifrs17.businessservice.api.dto.request;

/**
 * 표준 Request 의 options 영역 (설계서 5.3).
 */
public class RequestOptions {

    /** 응답 메시지 Locale. 기본 ko-KR. */
    private String locale = "ko-KR";

    /** 상세 결과 포함 여부. 기본 false. */
    private boolean includeDetails = false;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isIncludeDetails() {
        return includeDetails;
    }

    public void setIncludeDetails(boolean includeDetails) {
        this.includeDetails = includeDetails;
    }
}
