package com.koreanre.ifrs17.businessservice.console;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Business Service Console 검증 테스트 (설계서 8.7 인수 기준 CON-ACC-01 ~ CON-ACC-05).
 *
 * <p>In-Memory Catalog 를 변경하므로 클래스 종료 후 Context 를 재생성한다.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ServiceConsoleControllerTest {

    private static final String CONSOLE = "/api/business-service-console/v1/services";
    private static final String EXECUTE = "/api/business-services/v1/{serviceId}:execute";

    @Autowired
    private MockMvc mockMvc;

    private String form(String serviceId, String bean, String serviceName, boolean useYn) {
        return "{"
                + "\"serviceId\":\"" + serviceId + "\","
                + "\"serviceName\":\"" + serviceName + "\","
                + "\"version\":\"1.0\","
                + "\"implementationBean\":\"" + bean + "\","
                + "\"description\":\"테스트용 서비스 명세\","
                + "\"requestSchema\":\"{closingYearMonth:string}\","
                + "\"responseSchema\":\"{overallStatus:string}\","
                + "\"requiredRoles\":[\"IFRS17_USER\"],"
                + "\"useYn\":" + useYn
                + "}";
    }

    @Test
    @DisplayName("[CON-ACC-01] 배포되지 않은 Bean 으로 저장하면 차단되고 오류 원인이 표시된다")
    void save_beanNotFound() throws Exception {
        mockMvc.perform(post(CONSOLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "CONSOLE-CLIENT")
                        .content(form("IFRS17.NOTDEPLOYED.STATUS", "notDeployedBean", "미배포 서비스", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error.code").value("BS-VAL-001"))
                .andExpect(jsonPath("$.error.errorId").exists())
                .andExpect(jsonPath("$.error.details[0].field").value("implementationBean"));
    }

    @Test
    @DisplayName("[CON-ACC-01] Bean 의 Service ID 가 일치하지 않으면 저장이 차단된다")
    void save_serviceIdMismatch() throws Exception {
        mockMvc.perform(post(CONSOLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "CONSOLE-CLIENT")
                        .content(form("IFRS17.CSM.STATUS", "dummyClosingStatusHandler", "CSM 상태 조회", true)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BS-VAL-001"))
                .andExpect(jsonPath("$.error.details[0].field").value("serviceId"));
    }

    @Test
    @DisplayName("[CON-ACC-04] 명세를 수정한 뒤 상세 조회하면 변경내용이 동일하게 표시된다")
    void save_thenDetail() throws Exception {
        mockMvc.perform(post(CONSOLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "CONSOLE-CLIENT")
                        .content(form("IFRS17.STATEMENT.STATUS", "dummyStatementStatusHandler",
                                "재무제표 산출 상태 조회(수정)", true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.saved").value(true))
                .andExpect(jsonPath("$.result.mode").value("UPDATE"))
                .andExpect(jsonPath("$.result.beanValidation").value("PASSED"));

        mockMvc.perform(get(CONSOLE + "/{serviceId}", "IFRS17.STATEMENT.STATUS")
                        .header("X-Client-ID", "CONSOLE-CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.serviceName").value("재무제표 산출 상태 조회(수정)"))
                .andExpect(jsonPath("$.result.description").value("테스트용 서비스 명세"));
    }

    @Test
    @DisplayName("[CON-ACC-02/03] 미사용 전환 시 공통 Endpoint 가 차단되고, 사용 전환 시 다시 호출된다")
    void changeActive_blocksAndAllowsEndpoint() throws Exception {
        String body = "{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}";

        // 미사용 전환 -> 공통 Endpoint 차단 (CON-ACC-03)
        mockMvc.perform(patch(CONSOLE + "/{serviceId}/active", "IFRS17.EXPENSE.STATUS")
                        .param("use", "false")
                        .header("X-Client-ID", "CONSOLE-CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.active").value(false));

        mockMvc.perform(post(EXECUTE, "IFRS17.EXPENSE.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("BS-SVC-404"));

        // 사용 전환 -> 공통 Endpoint 허용 (CON-ACC-02)
        mockMvc.perform(patch(CONSOLE + "/{serviceId}/active", "IFRS17.EXPENSE.STATUS")
                        .param("use", "true")
                        .header("X-Client-ID", "CONSOLE-CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.active").value(true));

        mockMvc.perform(post(EXECUTE, "IFRS17.EXPENSE.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "TEST-CLIENT")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("[CON-ACC-05] 서비스 Test 화면은 운영과 동일 경로로 실행되고 처리시간을 반환한다")
    void serviceTest_delegatesToExecutor() throws Exception {
        mockMvc.perform(post(CONSOLE + "/{serviceId}/test", "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "CONSOLE-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-06\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.serviceId").value("IFRS17.CLOSING.STATUS"))
                .andExpect(jsonPath("$.elapsedMs").exists())
                .andExpect(jsonPath("$.requestId").exists());
    }

    @Test
    @DisplayName("[CON-ACC-05] 서비스 Test 에서 오류도 표준 오류코드로 확인된다")
    void serviceTest_error() throws Exception {
        mockMvc.perform(post(CONSOLE + "/{serviceId}/test", "IFRS17.CLOSING.STATUS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-ID", "CONSOLE-CLIENT")
                        .content("{\"serviceVersion\":\"1.0\",\"parameters\":{\"closingYearMonth\":\"2026-99\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BS-VAL-001"))
                .andExpect(jsonPath("$.error.errorId").exists());
    }

    @Test
    @DisplayName("서비스 목록 조회가 등록된 명세를 반환한다 (CON-01)")
    void list() throws Exception {
        mockMvc.perform(get(CONSOLE).header("X-Client-ID", "CONSOLE-CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.totalCount").value(6))
                .andExpect(jsonPath("$.result.services[0].serviceId").value("IFRS17.CLOSING.STATUS"));
    }

    @Test
    @DisplayName("미등록 서비스 상세 조회는 404 / BS-SVC-404 를 반환한다")
    void detail_notFound() throws Exception {
        mockMvc.perform(get(CONSOLE + "/{serviceId}", "IFRS17.UNKNOWN.SERVICE")
                        .header("X-Client-ID", "CONSOLE-CLIENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("BS-SVC-404"));
    }
}
