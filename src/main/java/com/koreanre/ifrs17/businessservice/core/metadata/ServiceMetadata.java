package com.koreanre.ifrs17.businessservice.core.metadata;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 서비스 Catalog 메타데이터 (설계서 7.2 BS_SERVICE / BS_SERVICE_VERSION, 부록 B 서비스 등록 Template).
 *
 * <p>Skeleton 단계에서는 DB 대신 In-Memory Catalog 로 관리한다.
 * 실제 구현 시 business_service.bs_service / bs_service_version 조회 결과로 대체한다.</p>
 */
public class ServiceMetadata {

    private final String serviceId;
    private final String serviceName;
    private final String domainCode;
    private final String serviceType;
    private final String sourceSystem;
    private final String ownerDepartment;
    private final String version;
    private final String implementationBean;
    private final int timeoutMs;
    private final Set<String> requiredRoles;
    private final String sensitivePolicy;
    private final boolean active;

    /**
     * [Draft/미정] 이 서비스가 호출할 IFRS17 배치 프로그램 ID.
     * 착수 분석(설계서 14장 No.1 "현행 매핑서")에서 확정되며, 현재는 임시값이다.
     */
    private final String legacyBatchProgramId;

    /**
     * [Draft/미정] 대응되는 현행 IFRS17 화면명.
     * 착수 분석에서 확정되며, 현재는 임시값이다.
     */
    private final String legacyScreenName;

    private ServiceMetadata(Builder builder) {
        this.serviceId = builder.serviceId;
        this.serviceName = builder.serviceName;
        this.domainCode = builder.domainCode;
        this.serviceType = builder.serviceType;
        this.sourceSystem = builder.sourceSystem;
        this.ownerDepartment = builder.ownerDepartment;
        this.version = builder.version;
        this.implementationBean = builder.implementationBean;
        this.timeoutMs = builder.timeoutMs;
        this.requiredRoles = Collections.unmodifiableSet(new LinkedHashSet<String>(builder.requiredRoles));
        this.sensitivePolicy = builder.sensitivePolicy;
        this.active = builder.active;
        this.legacyBatchProgramId = builder.legacyBatchProgramId;
        this.legacyScreenName = builder.legacyScreenName;
    }

    public static Builder builder(String serviceId) {
        return new Builder(serviceId);
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getOwnerDepartment() {
        return ownerDepartment;
    }

    public String getVersion() {
        return version;
    }

    public String getImplementationBean() {
        return implementationBean;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public Set<String> getRequiredRoles() {
        return requiredRoles;
    }

    public String getSensitivePolicy() {
        return sensitivePolicy;
    }

    public boolean isActive() {
        return active;
    }

    public String getLegacyBatchProgramId() {
        return legacyBatchProgramId;
    }

    public String getLegacyScreenName() {
        return legacyScreenName;
    }

    /** 부록 B 서비스 등록 Template 기준 Builder. */
    public static class Builder {
        private final String serviceId;
        private String serviceName;
        private String domainCode;
        private String serviceType = "READ";
        private String sourceSystem = "IFRS17";
        private String ownerDepartment = "정보기술팀";
        private String version = "1.0";
        private String implementationBean;
        private int timeoutMs = 30000;
        private Set<String> requiredRoles = new LinkedHashSet<String>();
        private String sensitivePolicy = "DEFAULT_MASKING";
        private boolean active = true;
        private String legacyBatchProgramId = "TBD";
        private String legacyScreenName = "TBD";

        private Builder(String serviceId) {
            this.serviceId = serviceId;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder domainCode(String domainCode) {
            this.domainCode = domainCode;
            return this;
        }

        public Builder serviceType(String serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder implementationBean(String implementationBean) {
            this.implementationBean = implementationBean;
            return this;
        }

        public Builder timeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder requiredRoles(String... roles) {
            this.requiredRoles = new LinkedHashSet<String>();
            Collections.addAll(this.requiredRoles, roles);
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder legacyBatchProgramId(String legacyBatchProgramId) {
            this.legacyBatchProgramId = legacyBatchProgramId;
            return this;
        }

        public Builder legacyScreenName(String legacyScreenName) {
            this.legacyScreenName = legacyScreenName;
            return this;
        }

        public ServiceMetadata build() {
            return new ServiceMetadata(this);
        }
    }
}
