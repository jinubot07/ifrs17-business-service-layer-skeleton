package com.koreanre.ifrs17.businessservice.core.dispatcher;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.domain.BusinessServiceHandler;

/**
 * serviceId 와 구현 Bean 매핑 컴포넌트 (설계서 4.2 - BusinessServiceDispatcher).
 *
 * <p>필수 인터페이스: {@code dispatch(ServiceContext, payload)}</p>
 */
public interface BusinessServiceDispatcher {

    /**
     * Service ID 에 해당하는 Handler Bean 을 반환한다.
     *
     * @param context 요청 Context (serviceId 보유)
     * @param payload 표준 Request 의 parameters
     * @return 등록된 Handler
     */
    BusinessServiceHandler<?, ?> dispatch(ServiceContext context, Object payload);
}
