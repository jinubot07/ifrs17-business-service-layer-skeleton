package com.koreanre.ifrs17.businessservice.api.dto.response;

/**
 * 필드 단위 오류 상세 (설계서 4.5 - 입력 오류 시 필드별 오류 반환).
 */
public class ErrorDetail {

    private String field;
    private String message;

    public ErrorDetail() {
    }

    public ErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
