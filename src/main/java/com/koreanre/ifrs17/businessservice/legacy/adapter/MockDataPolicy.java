package com.koreanre.ifrs17.businessservice.legacy.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <b>[Skeleton 전용]</b> Mock 데이터 보유 기준.
 *
 * <p>설계서 9장 "데이터 없음은 오류가 아니라 SUCCESS + 빈 결과" 규칙을 Postman 으로 검증할 수 있도록,
 * 아래 기준년월에 대해서만 하드코딩 데이터를 반환하고 그 외 유효한 기준년월은 빈 결과를 반환한다.</p>
 */
public final class MockDataPolicy {

    /** Mock 데이터를 보유한 기준년월. */
    private static final Set<String> AVAILABLE_YEAR_MONTHS =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("2026-06", "2026-05")));

    /** 결과 없음 경고 코드 (설계서 부록 A - BS-DATA-000). */
    public static final String NO_DATA_CODE = "BS-DATA-000";
    public static final String NO_DATA_MESSAGE = "해당 기준년월의 조회 결과가 없습니다.";

    private MockDataPolicy() {
    }

    public static boolean hasData(String closingYearMonth) {
        return AVAILABLE_YEAR_MONTHS.contains(closingYearMonth);
    }

    public static Set<String> availableYearMonths() {
        return AVAILABLE_YEAR_MONTHS;
    }
}
