package com.koreanre.ifrs17.businessservice.core.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 표준 Request 의 parameters(Map) 를 서비스별 요청 DTO 로 바인딩한다.
 *
 * <p>설계서 10.5 금지사항 "응답에 Entity/Map 을 무검증으로 직접 직렬화" 에 대응하여,
 * 입력도 Map 그대로 사용하지 않고 타입이 있는 DTO 로 변환한다.</p>
 */
@Component
public class ParameterBinder {

    private final ObjectMapper objectMapper;

    public ParameterBinder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T bind(Map<String, Object> parameters, Class<T> requestType) {
        try {
            return objectMapper.convertValue(parameters, requestType);
        } catch (IllegalArgumentException e) {
            throw ValidationException.of("parameters",
                    "업무 파라미터를 요청 규격으로 변환할 수 없습니다. (" + rootMessage(e) + ")");
        }
    }

    private String rootMessage(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}
