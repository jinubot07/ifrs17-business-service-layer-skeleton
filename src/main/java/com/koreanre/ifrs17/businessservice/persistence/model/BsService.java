package com.koreanre.ifrs17.businessservice.persistence.model;

import java.time.LocalDateTime;

/**
 * BS_SERVICE 테이블 모델 (설계서 7.2 주요 테이블 / 7.3 핵심 DDL).
 *
 * <p><b>[Skeleton]</b> 본 클래스는 설계서 7.3 DDL 컬럼과 1:1 대응하는 순수 Java POJO 이며,
 * DB 연결이나 ORM/MyBatis 애노테이션을 포함하지 않는다.
 * 실제 DB 연동 단계에서 Mapper 구현과 함께 사용한다.</p>
 */
public class BsService {

    private String serviceId;
    private String serviceName;
    private String domainCode;
    /** READ / ACTION (설계서 7.3 CHECK 제약). */
    private String serviceType;
    private String sourceSystem;
    private String ownerDepartment;
    /** Y / N */
    private String activeYn;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getOwnerDepartment() {
        return ownerDepartment;
    }

    public void setOwnerDepartment(String ownerDepartment) {
        this.ownerDepartment = ownerDepartment;
    }

    public String getActiveYn() {
        return activeYn;
    }

    public void setActiveYn(String activeYn) {
        this.activeYn = activeYn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
