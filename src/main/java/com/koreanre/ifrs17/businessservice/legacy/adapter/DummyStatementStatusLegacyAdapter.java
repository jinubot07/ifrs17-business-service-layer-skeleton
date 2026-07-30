package com.koreanre.ifrs17.businessservice.legacy.adapter;

import com.koreanre.ifrs17.businessservice.domain.common.LegacyBinding;
import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;
import com.koreanre.ifrs17.businessservice.domain.statement.StatementStatusRequest;
import com.koreanre.ifrs17.businessservice.domain.statement.StatementStatusResponse;
import com.koreanre.ifrs17.businessservice.domain.statement.StatementTypeStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IFRS17.STATEMENT.STATUS 용 Legacy Adapter 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> 기존 재무제표/리포트 Service 호출 대신 하드코딩 결과를 반환한다.</p>
 * <p><b>[Draft]</b> 배치 프로그램 ID·화면명 미확정(임시값).</p>
 */
@Component
public class DummyStatementStatusLegacyAdapter
        implements LegacyAdapter<StatementStatusRequest, StatementStatusResponse> {

    private static final String BATCH_PROGRAM_ID = "TBD-BAT-STATEMENT-STATUS-001";
    private static final String SCREEN_NAME = "TBD-재무제표산출현황조회";

    @Override
    public String legacyBatchProgramId() {
        return BATCH_PROGRAM_ID;
    }

    @Override
    public String legacyScreenName() {
        return SCREEN_NAME;
    }

    @Override
    public StatementStatusResponse invoke(StatementStatusRequest request) {
        System.out.println("[BSL-LEGACY] adapter=DummyStatementStatusLegacyAdapter"
                + ", batchProgramId=" + BATCH_PROGRAM_ID
                + ", closingYearMonth=" + request.getClosingYearMonth() + " (Mock 호출)");

        StatementStatusResponse response = new StatementStatusResponse();
        response.setClosingYearMonth(request.getClosingYearMonth());
        response.setBaseDateTime("2026-07-01T21:00:00+09:00");
        response.setLegacyBinding(new LegacyBinding(BATCH_PROGRAM_ID, SCREEN_NAME));

        if (!MockDataPolicy.hasData(request.getClosingYearMonth())) {
            response.setStatements(new ArrayList<StatementTypeStatus>());
            response.setOverallStatus(ProcessingStatusCode.NOT_STARTED);
            response.setResultCount(0);
            return response;
        }

        List<StatementTypeStatus> all = Arrays.asList(
                new StatementTypeStatus("BS", "재무상태표", ProcessingStatusCode.COMPLETED,
                        "1.2", "2026-07-01T20:55:00+09:00", 0),
                new StatementTypeStatus("IS", "포괄손익계산서", ProcessingStatusCode.COMPLETED,
                        "1.2", "2026-07-01T20:56:12+09:00", 0),
                new StatementTypeStatus("DISCLOSURE", "주석/공시", ProcessingStatusCode.NOT_STARTED,
                        null, null, 0)
        );

        List<StatementTypeStatus> filtered = new ArrayList<StatementTypeStatus>();
        for (StatementTypeStatus item : all) {
            if (!StringUtils.hasText(request.getStatementType())
                    || item.getStatementType().equalsIgnoreCase(request.getStatementType())) {
                filtered.add(item);
            }
        }

        response.setStatements(filtered);
        response.setResultCount(filtered.size());
        response.setErrorCount(0);
        response.setOverallStatus(filtered.isEmpty() ? ProcessingStatusCode.NOT_STARTED
                : ProcessingStatusCode.PARTIAL);
        return response;
    }
}
