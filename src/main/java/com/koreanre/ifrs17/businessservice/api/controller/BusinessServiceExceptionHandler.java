package com.koreanre.ifrs17.businessservice.api.controller;

import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.core.context.IdGenerator;
import com.koreanre.ifrs17.businessservice.core.context.RequestContextResolver;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.response.StandardResponseBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Executor 진입 이전(Servlet/Spring MVC 단계)에서 발생하는 오류를 표준 Error Response 로 변환한다.
 *
 * <p>설계서 5.5 표준 Error Response 규격은 Framework 오류에도 동일하게 적용한다.</p>
 */
@RestControllerAdvice
public class BusinessServiceExceptionHandler {

    private final RequestContextResolver requestContextResolver;
    private final StandardResponseBuilder responseBuilder;
    private final IdGenerator idGenerator;

    public BusinessServiceExceptionHandler(RequestContextResolver requestContextResolver,
                                           StandardResponseBuilder responseBuilder,
                                           IdGenerator idGenerator) {
        this.requestContextResolver = requestContextResolver;
        this.responseBuilder = responseBuilder;
        this.idGenerator = idGenerator;
    }

    /** 잘못된 JSON Body (설계서 4.5 입력 오류 / BS-VAL-001). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardResponse<?>> handleUnreadable(HttpMessageNotReadableException e, WebRequest request) {
        return build(request, ErrorCode.BS_VAL_001, "요청 JSON 형식이 올바르지 않습니다.");
    }

    /** 미정의 URI (설계서 4.5 / BS-SVC-404). */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<StandardResponse<?>> handleNoHandler(NoHandlerFoundException e, WebRequest request) {
        return build(request, ErrorCode.BS_SVC_404, "요청한 Endpoint 를 찾을 수 없습니다. " + e.getRequestURL());
    }

    /** 그 밖의 미분류 오류 (설계서 4.5 / BS-SYS-500). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<?>> handleUnexpected(Exception e, WebRequest request) {
        e.printStackTrace(System.out);
        return build(request, ErrorCode.BS_SYS_500, ErrorCode.BS_SYS_500.defaultMessage());
    }

    private ResponseEntity<StandardResponse<?>> build(WebRequest request, ErrorCode errorCode, String message) {
        HttpServletRequest httpRequest = (request instanceof ServletWebRequest)
                ? ((ServletWebRequest) request).getRequest() : null;
        ServiceContext context = requestContextResolver.resolve(httpRequest, null, null);
        String errorId = idGenerator.newErrorId();

        System.out.println("[BSL-ERROR] errorId=" + errorId
                + ", requestId=" + context.getRequestId()
                + ", code=" + errorCode.code() + ", message=" + message);

        StandardResponse<Object> body = responseBuilder.error(context, errorCode, message, errorId, null, 0L);
        HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.status(HttpStatus.valueOf(errorCode.httpStatus())).headers(headers)
                .body((StandardResponse<?>) body);
    }
}
