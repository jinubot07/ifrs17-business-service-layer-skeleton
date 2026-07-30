package com.koreanre.ifrs17.businessservice.core.validator;

import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * 공통 입력 검증 유틸리티 (설계서 10.2 Reference Implementation 의 ValidationUtils).
 */
public final class ValidationUtils {

    /** 기준년월 형식: YYYY-MM (설계서 9.1~9.5). */
    private static final Pattern YEAR_MONTH = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    private ValidationUtils() {
    }

    /**
     * 기준년월(YYYY-MM) 필수/형식 검증.
     *
     * @throws ValidationException BS-VAL-001
     */
    public static void requireYearMonth(String value) {
        requireYearMonth("closingYearMonth", value);
    }

    public static void requireYearMonth(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw ValidationException.of(field, "필수 항목입니다. (형식: YYYY-MM)");
        }
        if (!YEAR_MONTH.matcher(value).matches()) {
            throw ValidationException.of(field, "기준년월 형식이 올바르지 않습니다. (형식: YYYY-MM, 입력값: " + value + ")");
        }
    }

    /** 필수 문자열 검증. */
    public static void requireText(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw ValidationException.of(field, "필수 항목입니다.");
        }
    }

    /** 길이 제한 검증 (BS-VAL-002 허용범위 초과). */
    public static void maxLength(String field, String value, int max) {
        if (value != null && value.length() > max) {
            throw ValidationException.of(field, "허용 길이(" + max + ")를 초과했습니다.");
        }
    }
}
