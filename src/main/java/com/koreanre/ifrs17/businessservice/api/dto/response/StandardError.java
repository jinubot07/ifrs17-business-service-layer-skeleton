package com.koreanre.ifrs17.businessservice.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * 표준 Error Response 의 error 영역 (설계서 5.5).
 *
 * <p>외부에는 Stack Trace 를 노출하지 않고 errorId 만 전달한다(설계서 6.4).</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardError {

    /** 표준 오류코드 (부록 A). */
    private String code;

    /** 사용자 노출 메시지. */
    private String message;

    /** 서버 로그 추적용 Error ID. */
    private String errorId;

    /** 필드 단위 오류 상세 (설계서 4.5 입력 오류). */
    private List<ErrorDetail> details = new ArrayList<ErrorDetail>();

    public StandardError() {
    }

    public StandardError(String code, String message, String errorId) {
        this.code = code;
        this.message = message;
        this.errorId = errorId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ErrorDetail> details) {
        this.details = (details == null) ? new ArrayList<ErrorDetail>() : details;
    }
}
