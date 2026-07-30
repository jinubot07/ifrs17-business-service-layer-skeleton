package com.koreanre.ifrs17.businessservice.core.dispatcher;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.ServiceNotFoundException;
import com.koreanre.ifrs17.businessservice.domain.BusinessServiceHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * BusinessServiceDispatcher 기본 구현체.
 *
 * <p>Application 기동 시 Spring Container 에 등록된 모든 {@link BusinessServiceHandler} Bean 을
 * serviceId 기준으로 Registry 에 적재한다(설계서 8.4 - Bean 존재/Interface 구현/Service ID 일치 검증).</p>
 */
@Component
public class DefaultBusinessServiceDispatcher implements BusinessServiceDispatcher {

    private final List<BusinessServiceHandler<?, ?>> handlers;
    private final Map<String, BusinessServiceHandler<?, ?>> registry =
            new LinkedHashMap<String, BusinessServiceHandler<?, ?>>();

    public DefaultBusinessServiceDispatcher(List<BusinessServiceHandler<?, ?>> handlers) {
        this.handlers = handlers;
    }

    @PostConstruct
    void initRegistry() {
        for (BusinessServiceHandler<?, ?> handler : handlers) {
            BusinessServiceHandler<?, ?> duplicated = registry.put(handler.serviceId(), handler);
            if (duplicated != null) {
                throw new IllegalStateException("중복된 serviceId Handler 가 등록되었습니다: " + handler.serviceId());
            }
            System.out.println("[BSL-REGISTRY] serviceId=" + handler.serviceId()
                    + ", bean=" + handler.getClass().getSimpleName());
        }
    }

    @Override
    public BusinessServiceHandler<?, ?> dispatch(ServiceContext context, Object payload) {
        BusinessServiceHandler<?, ?> handler = registry.get(context.getServiceId());
        if (handler == null) {
            // Catalog 에는 있으나 구현 Bean 이 배포되지 않은 경우 (설계서 8.7 CON-ACC-01)
            throw new ServiceNotFoundException("등록된 구현 Bean 이 없습니다. serviceId=" + context.getServiceId());
        }
        return handler;
    }

    /** 등록된 Handler 목록 (운영 점검용). */
    public Map<String, BusinessServiceHandler<?, ?>> getRegistry() {
        return registry;
    }
}
