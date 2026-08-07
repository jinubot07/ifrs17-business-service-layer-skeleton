package com.koreanre.ifrs17.businessservice.core.validator;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 표준 Request 자체(Envelope)와 필수 업무 파라미터에 대한 공통 검증.
 *
 * <p>기능정의서 v2.7 - 7단계 (1) 입력 Schema 및 업무 파라미터 검증.
 * BS_SERVICE_PARAM (PK service_id + version + param_name) 에서 요청 serviceId 와 확정 version 의
 * 파라미터 정의 목록을 조회하고, required_yn 이 'Y' 인 필수 파라미터가 Request 의 parameters 에
 * 모두 입력되었는지 확인한다. 미입력 시 BS-VAL-001(400).</p>
 *
 * <p>파라미터의 타입·형식 검증은 별도 단계로 두지 않는다. 타입 불일치는 7단계 (2) DTO 바인딩 시
 * 변환 실패로 걸러진다. 허용값·허용범위 검증은 서비스별 업무 규칙이므로 각 Handler 의
 * {@code validate()} 에서 수행한다(설계서 10.2).</p>
 *
 * <p><b>[Mock]</b> BS_SERVICE_PARAM 테이블 대신 In-Memory 정의를 사용한다.
 * DB 연동 시 BsServiceParamMapper 조회로 교체한다.</p>
 */
@Component
public class StandardRequestValidator {

    /** [Mock] BS_SERVICE_PARAM - "serviceId:version" -> required_yn = 'Y' 인 param_name 목록. */
    private static final Map<String, List<String>> REQUIRED_PARAMS = new LinkedHashMap<String, List<String>>();

    static {
        for (String serviceId : new String[]{
                "IFRS17.CLOSING.STATUS", "IFRS17.JOURNAL.STATUS", "IFRS17.EXPENSE.STATUS",
                "IFRS17.STATEMENT.STATUS", "IFRS17.CSM.STATUS"}) {
            REQUIRED_PARAMS.put(serviceId + ":1.0", Arrays.asList("closingYearMonth"));
        }
    }

    public void validate(StandardRequest request, ServiceMetadata metadata) {
        if (request == null) {
            throw new ValidationException("요청 Body 가 없습니다.");
        }
        if (request.getParameters() == null || request.getParameters().isEmpty()) {
            throw ValidationException.of("parameters", "업무 파라미터가 비어 있습니다.");
        }
        // serviceVersion 은 선택 입력이나, 입력된 경우 Catalog 활성 버전과 일치해야 한다(설계서 5.6).
        if (StringUtils.hasText(request.getServiceVersion())
                && !request.getServiceVersion().equals(metadata.getVersion())) {
            throw ValidationException.of("serviceVersion",
                    "요청 버전이 활성 버전과 일치하지 않습니다. (요청: " + request.getServiceVersion()
                            + ", 활성: " + metadata.getVersion() + ")");
        }

        // (1) BS_SERVICE_PARAM 의 required_yn = 'Y' 파라미터가 모두 입력되었는지 확인한다.
        for (String paramName : requiredParamsOf(metadata)) {
            Object value = request.getParameters().get(paramName);
            if (value == null || !StringUtils.hasText(String.valueOf(value))) {
                throw ValidationException.of(paramName, "필수 파라미터가 입력되지 않았습니다.");
            }
        }
    }

    /** BS_SERVICE_PARAM 조회 대응. 정의가 없으면 필수 파라미터가 없는 것으로 본다. */
    private List<String> requiredParamsOf(ServiceMetadata metadata) {
        List<String> params = REQUIRED_PARAMS.get(metadata.getServiceId() + ":" + metadata.getVersion());
        return params == null ? Collections.<String>emptyList() : params;
    }
}
