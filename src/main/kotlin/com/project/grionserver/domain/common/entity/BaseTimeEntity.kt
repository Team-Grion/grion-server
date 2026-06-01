package com.project.grionserver.domain.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {
    @CreatedDate
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",
    )
    @Comment("생성 일시")
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
    )
    @Comment("수정 일시")
    var updatedAt: LocalDateTime? = null
        protected set
}

// 수정 시간 기능이 없는 테이블(Message, PetImage)용
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseCreatedAtEntity {
    @CreatedDate
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",
    )
    @Comment("생성 일시")
    var createdAt: LocalDateTime? = null
        protected set
}
