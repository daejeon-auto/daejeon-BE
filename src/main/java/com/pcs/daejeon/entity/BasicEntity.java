package com.pcs.daejeon.entity;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BasicEntity extends BasicTime {
    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
