package com.koreanre.ifrs17.businessservice.api.dto.response;

/**
 * 성공 응답의 경고 항목 (설계서 5.4 warnings).
 *
 * <p>예: 업무 데이터 없음(BS-DATA-000)은 오류가 아니라 성공 + 빈 결과 + 경고로 반환한다(설계서 4.5).</p>
 */
public class Warning {

    private String code;
    private String message;

    public Warning() {
    }

    public Warning(String code, String message) {
        this.code = code;
        this.message = message;
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
}
