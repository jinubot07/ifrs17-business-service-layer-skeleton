package com.koreanre.ifrs17.businessservice.core.context;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Request ID / Trace ID / Error ID 생성기.
 *
 * <p>설계서 5.2 · 5.4 · 5.5 예시 형식을 따른다.
 * (REQ-20260714-0001 / TRACE-20260714-0001 / ERR-20260714-39281)</p>
 *
 * <p>Skeleton 단계에서는 WAS 단일 인스턴스 기준의 In-Memory Sequence 를 사용한다.
 * 운영 다중 WAS 환경에서는 server_instance 를 포함한 채번 정책으로 대체해야 한다.</p>
 */
@Component
public class IdGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AtomicInteger requestSequence = new AtomicInteger(0);
    private final AtomicInteger traceSequence = new AtomicInteger(0);
    private final AtomicInteger errorSequence = new AtomicInteger(0);

    public String newRequestId() {
        return String.format("REQ-%s-%04d", today(), next(requestSequence, 9999));
    }

    public String newTraceId() {
        return String.format("TRACE-%s-%04d", today(), next(traceSequence, 9999));
    }

    public String newErrorId() {
        return String.format("ERR-%s-%05d", today(), next(errorSequence, 99999));
    }

    private String today() {
        return LocalDate.now().format(DATE);
    }

    private int next(AtomicInteger sequence, int max) {
        int value = sequence.incrementAndGet();
        if (value > max) {
            sequence.set(1);
            value = 1;
        }
        return value;
    }
}
