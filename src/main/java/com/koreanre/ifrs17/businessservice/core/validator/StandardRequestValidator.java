package com.koreanre.ifrs17.businessservice.core.validator;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 표준 Request 자체(Envelope)에 대한 공통 검증 (설계서 4.3 - 7단계 입력 Schema 검증).
 *
 * <p>업무 파라미터 검증은 각 Handler 의 {@code validate()} 에서 수행한다.</p>
 */
@Component
public class StandardRequestValidator {

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
    }
}
