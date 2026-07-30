package com.koreanre.ifrs17.businessservice.legacy.adapter;

import com.koreanre.ifrs17.businessservice.domain.closing.ClosingStageStatus;
import com.koreanre.ifrs17.businessservice.domain.closing.ClosingStatusRequest;
import com.koreanre.ifrs17.businessservice.domain.closing.ClosingStatusResponse;
import com.koreanre.ifrs17.businessservice.api.dto.response.LegacyBinding;
import com.koreanre.ifrs17.businessservice.api.dto.response.ProcessingStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IFRS17.CLOSING.STATUS 용 Legacy Adapter 의 Skeleton 구현체 (설계서 10.3 참조).
 *
 * <p><b>[Mock]</b> 기존 결산 상태 Service / 배치 관리 Service 호출 대신 하드코딩 결과를 반환한다.</p>
 * <p><b>[Draft]</b> 호출 대상 배치 프로그램 ID와 화면명은 미확정이므로 임시값을 사용한다.</p>
 *
 * <p>실제 구현 시:</p>
 * <pre>
 * LegacyClosingStatus legacy = existingClosingService.findClosingStatus(yearMonth);
 * return ClosingStatusMapper.toResponse(legacy);
 * </pre>
 */
@Component
public class DummyClosingStatusLegacyAdapter implements LegacyAdapter<ClosingStatusRequest, ClosingStatusResponse> {

    /** [Draft] 미확정 - 설계서 14장 No.1 현행 매핑서에서 확정. */
    private static final String BATCH_PROGRAM_ID = "TBD-BAT-CLOSING-STATUS-001";
    /** [Draft] 미확정 - 현행 화면명. */
    private static final String SCREEN_NAME = "TBD-결산진행현황조회";

    @Override
    public String legacyBatchProgramId() {
        return BATCH_PROGRAM_ID;
    }

    @Override
    public String legacyScreenName() {
        return SCREEN_NAME;
    }

    @Override
    public ClosingStatusResponse invoke(ClosingStatusRequest request) {
        System.out.println("[BSL-LEGACY] adapter=DummyClosingStatusLegacyAdapter"
                + ", batchProgramId=" + BATCH_PROGRAM_ID
                + ", screenName=" + SCREEN_NAME
                + ", closingYearMonth=" + request.getClosingYearMonth()
                + " (Mock 호출 - 실제 Legacy Service 미연동)");

        ClosingStatusResponse response = new ClosingStatusResponse();
        response.setClosingYearMonth(request.getClosingYearMonth());
        response.setClosingType(StringUtils.hasText(request.getClosingType()) ? request.getClosingType() : "MONTHLY");
        response.setBaseDateTime("2026-07-01T21:00:00+09:00");
        response.setLegacyBinding(new LegacyBinding(BATCH_PROGRAM_ID, SCREEN_NAME));

        if (!MockDataPolicy.hasData(request.getClosingYearMonth())) {
            // 데이터 없음 -> 오류가 아니라 빈 결과 (설계서 9.1)
            response.setStages(new ArrayList<ClosingStageStatus>());
            response.setOverallStatus(ProcessingStatusCode.NOT_STARTED);
            response.setResultCount(0);
            response.setErrorCount(0);
            response.setProgressRate(0.0);
            return response;
        }

        List<ClosingStageStatus> stages = Arrays.asList(
                new ClosingStageStatus("CL010", "결산기초자료 적재", ProcessingStatusCode.COMPLETED,
                        "2026-07-01T19:00:00+09:00", "2026-07-01T19:24:31+09:00", 100.0, 0),
                new ClosingStageStatus("CL020", "계약 그룹핑", ProcessingStatusCode.COMPLETED,
                        "2026-07-01T19:25:00+09:00", "2026-07-01T19:58:12+09:00", 100.0, 0),
                new ClosingStageStatus("CL030", "측정단위 산출", ProcessingStatusCode.COMPLETED,
                        "2026-07-01T20:00:00+09:00", "2026-07-01T20:41:07+09:00", 100.0, 0),
                new ClosingStageStatus("CL040", "CSM 산출", ProcessingStatusCode.RUNNING,
                        "2026-07-01T20:45:00+09:00", null, 62.5, 0),
                new ClosingStageStatus("CL050", "전표 생성", ProcessingStatusCode.NOT_STARTED,
                        null, null, 0.0, 0),
                new ClosingStageStatus("CL060", "재무제표 산출", ProcessingStatusCode.NOT_STARTED,
                        null, null, 0.0, 0)
        );

        response.setStages(new ArrayList<ClosingStageStatus>(stages));
        response.setOverallStatus(ProcessingStatusCode.RUNNING);
        response.setProgressRate(60.4);
        response.setResultCount(stages.size());
        response.setErrorCount(0);
        return response;
    }
}
