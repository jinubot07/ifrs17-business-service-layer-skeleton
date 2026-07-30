package com.koreanre.ifrs17.businessservice.console;

import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;
import com.koreanre.ifrs17.businessservice.core.workflow.BusinessServiceHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 서비스 명세 저장 시 자동 검증 (설계서 8.1 / 8.4 - 4단계).
 *
 * <p>검증 항목</p>
 * <ol>
 *   <li>Spring Bean 존재 여부</li>
 *   <li>{@link BusinessServiceHandler} 구현 여부</li>
 *   <li>Service ID 일치 여부</li>
 * </ol>
 *
 * <p>설계서 8.7 CON-ACC-01: 등록된 Bean 이 없으면 저장 또는 사용 전환이 차단되고 오류 원인이 표시된다.</p>
 */
@Component
public class ServiceSpecificationValidator {

    /** Service ID 규칙: 대문자·숫자·점 구분 (설계서 8.3). */
    private static final Pattern SERVICE_ID = Pattern.compile("^[A-Z0-9]+(\\.[A-Z0-9_]+)+$");

    private final ApplicationContext applicationContext;

    public ServiceSpecificationValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 명세 저장 전 검증.
     *
     * @throws ValidationException BS-VAL-001
     */
    public void validate(ServiceSpecificationForm form) {
        requireText("serviceId", form.getServiceId());
        requireText("serviceName", form.getServiceName());
        requireText("version", form.getVersion());
        requireText("implementationBean", form.getImplementationBean());
        requireText("description", form.getDescription());
        requireText("requestSchema", form.getRequestSchema());
        requireText("responseSchema", form.getResponseSchema());

        if (!SERVICE_ID.matcher(form.getServiceId()).matches()) {
            throw ValidationException.of("serviceId",
                    "Service ID 는 대문자와 점(.) 구분 형식이어야 합니다. (예: IFRS17.CLOSING.STATUS)");
        }
        if (form.getServiceName().length() > 100) {
            throw ValidationException.of("serviceName", "서비스명은 100자 이내여야 합니다.");
        }
        if (form.getRequiredRoles().isEmpty()) {
            throw ValidationException.of("requiredRoles", "허용 역할을 1개 이상 지정해야 합니다.");
        }

        // [Skeleton] 설계서 8.3 "버전 중복 금지"는 BS_SERVICE_VERSION 이 서비스별 다중 버전을
        // 보관할 때 적용된다. 현재 In-Memory Catalog 는 서비스당 1건만 보관하므로
        // 동일 serviceId + 동일 version 저장은 중복 등록이 아니라 수정(UPDATE)으로 처리한다.
        validateBean(form);
    }

    /** Bean 존재 / Interface 구현 / Service ID 일치 검증 (설계서 8.4 - 4단계). */
    private void validateBean(ServiceSpecificationForm form) {
        String beanName = form.getImplementationBean();
        if (!applicationContext.containsBean(beanName)) {
            throw ValidationException.of("implementationBean",
                    "WAS 에 배포된 Spring Bean 이 없습니다. beanName=" + beanName
                            + " (Java 구현체 배포 후 등록해야 합니다.)");
        }

        Object bean = applicationContext.getBean(beanName);
        if (!(bean instanceof BusinessServiceHandler)) {
            throw ValidationException.of("implementationBean",
                    "해당 Bean 이 BusinessServiceHandler 를 구현하지 않았습니다. beanName=" + beanName);
        }

        String handlerServiceId = ((BusinessServiceHandler<?, ?>) bean).serviceId();
        if (!form.getServiceId().equals(handlerServiceId)) {
            throw ValidationException.of("serviceId",
                    "Handler 의 Service ID 와 일치하지 않습니다. (입력: " + form.getServiceId()
                            + ", Handler: " + handlerServiceId + ")");
        }
    }

    private void requireText(String field, String value) {
        if (!StringUtils.hasText(value)) {
            throw ValidationException.of(field, "필수 항목입니다.");
        }
    }
}
