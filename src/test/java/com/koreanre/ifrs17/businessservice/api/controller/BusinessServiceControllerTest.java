package com.koreanre.ifrs17.businessservice.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Skeleton 통신 검증 테스트.
 *
 * <p>설계서 11.2 필수 인수 시나리오를 Mock 수준에서 확인하고,
 * 개발 지시서 5장 "최종 검증 방법(Definition of Done)"을 자동화한다.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class BusinessServiceControllerTest {

    private static final String EXECUTE_URI = "/api/business-services/v1/{serviceId}:execute";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("[DoD] IFRS17.CLOSING.STATUS 정상 호출 시 표준 Success Response(HTTP 200)를 반환한다")
    void executeClosingStatus_success() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.serviceId").value("IFRS17.CLOSING.STATUS"))
                .andExpect(jsonPath("$.serviceVersion").value("1.0"))
                .andExpect(jsonPath("$.sourceSystem").value("IFRS17"))
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.processedAt").exists())
                .andExpect(jsonPath("$.elapsedMs").exists())
                .andExpect(jsonPath("$.result.closingYearMonth").value("2026-06"))
                .andExpect(jsonPath("$.result.overallStatus").value("RUNNING"))
                .andExpect(jsonPath("$.result.stages.length()").value(6))
                .andExpect(jsonPath("$.result.legacyBinding.batchProgramId").value("TBD-BAT-CLOSING-STATUS-001"))
                .andExpect(jsonPath("$.warnings").isArray());
    }

    @Test
    @DisplayName("X-Request-ID Header 를 보내면 응답에 그대로 사용된다 (설계서 5.2)")
    void executeClosingStatus_usesProvidedRequestId() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .header("X-Request-ID", "REQ-20260714-0001")
                        .header("X-User-ID", "E12345")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value("REQ-20260714-0001"));
    }

    @Test
    @DisplayName("파일럿 5종 서비스가 모두 표준 Success Response 를 반환한다 (설계서 9장)")
    void executeAllPilotServices() throws Exception {
        String[] serviceIds = {
                "IFRS17.CLOSING.STATUS",
                "IFRS17.JOURNAL.STATUS",
                "IFRS17.EXPENSE.STATUS",
                "IFRS17.STATEMENT.STATUS",
                "IFRS17.CSM.STATUS"
        };
        for (String serviceId : serviceIds) {
            mockMvc.perform(post(EXECUTE_URI, serviceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Client-ID", "TEST-CLIENT")
                            .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"))
                    .andExpect(jsonPath("$.serviceId").value(serviceId))
                    .andExpect(jsonPath("$.result.closingYearMonth").value("2026-06"));
        }
    }

    @Test
    @DisplayName("데이터 없음은 오류가 아니라 SUCCESS + 빈 결과 + BS-DATA-000 경고이다 (설계서 4.5 / 9.1)")
    void executeClosingStatus_noData() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2020-01\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.stages.length()").value(0))
                .andExpect(jsonPath("$.result.resultCount").value(0))
                .andExpect(jsonPath("$.warnings[0].code").value("BS-DATA-000"));
    }

    @Test
    @DisplayName("잘못된 기준년월은 400 / BS-VAL-001 로 반환한다 (설계서 11.2 입력 오류)")
    void executeClosingStatus_validationError() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-13\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("BS-VAL-001"))
                .andExpect(jsonPath("$.error.errorId").exists())
                .andExpect(jsonPath("$.error.details[0].field").value("closingYearMonth"));
    }

    @Test
    @DisplayName("필수 Header(X-Client-ID) 누락은 401 / BS-AUTH-001 로 반환한다 (설계서 5.2 / 11.2)")
    void executeClosingStatus_authenticationError() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("BS-AUTH-001"));
    }

    @Test
    @DisplayName("미등록 서비스는 404 / BS-SVC-404 로 반환한다 (설계서 11.2 서비스 비활성)")
    void executeUnknownService() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.UNKNOWN.SERVICE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("BS-SVC-404"));
    }

    @Test
    @DisplayName("비활성(미사용) 서비스는 404 / BS-SVC-404 로 차단한다 (설계서 8.7 CON-ACC-03)")
    void executeDisabledService() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.SAMPLE.DISABLED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("BS-SVC-404"));
    }

    @Test
    @DisplayName("활성 버전과 다른 serviceVersion 은 404 / BS-SVC-404 로 반환한다 (설계서 5.6)")
    void executeWrongVersion() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"9.9\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("BS-SVC-404"));
    }

    @Test
    @DisplayName("[시뮬레이션] 권한 없음은 403 / BS-AUTH-003 로 반환한다 (설계서 11.2)")
    void simulateForbidden() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":"
                                + "{\"closingYearMonth\":\"2026-06\",\"__simulate\":\"FORBIDDEN\"}}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("BS-AUTH-003"));
    }

    @Test
    @DisplayName("[시뮬레이션] Timeout 은 504 / BS-SYS-504 로 반환한다 (설계서 11.2)")
    void simulateTimeout() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":"
                                + "{\"closingYearMonth\":\"2026-06\",\"__simulate\":\"TIMEOUT\"}}"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.error.code").value("BS-SYS-504"));
    }

    @Test
    @DisplayName("[시뮬레이션] Legacy 오류는 500 / BS-LEG-500 + Error ID 로 반환한다 (설계서 11.2)")
    void simulateLegacyError() throws Exception {
        mockMvc.perform(post(EXECUTE_URI, "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":"
                                + "{\"closingYearMonth\":\"2026-06\",\"__simulate\":\"LEGACY_ERROR\"}}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value("BS-LEG-500"))
                .andExpect(jsonPath("$.error.errorId").exists());
    }

    @Test
    @DisplayName("Catalog 목록 조회가 파일럿 5종 + 테스트용 비활성 서비스를 반환한다 (설계서 5.1)")
    void catalog() throws Exception {
        mockMvc.perform(get("/api/business-services/v1/catalog")
                        .header("X-Client-ID", "TEST-CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.totalCount").value(6))
                .andExpect(jsonPath("$.result.services[0].serviceId").value("IFRS17.CLOSING.STATUS"));
    }

    @Test
    @DisplayName("Catalog 단건 조회가 서비스 명세를 반환한다 (설계서 5.1 / 8.3)")
    void catalogDetail() throws Exception {
        mockMvc.perform(get("/api/business-services/v1/catalog/{serviceId}", "IFRS17.CSM.STATUS")
                        .header("X-Client-ID", "TEST-CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.serviceId").value("IFRS17.CSM.STATUS"))
                .andExpect(jsonPath("$.result.implementationBean").value("dummyCsmStatusHandler"))
                .andExpect(jsonPath("$.result.timeoutMs").value(30000))
                .andExpect(jsonPath("$.result.legacyBatchProgramId").value("TBD-BAT-CSM-STATUS-001"));
    }
}
