package com.project.grionserver.domain.create.entity

import com.project.grionserver.domain.common.entity.BaseTimeEntity
import com.project.grionserver.domain.pet.entity.Pet
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "ai_image_task")
class CreateImageTask(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("작업 PK")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, foreignKey = ForeignKey(name = "FK_AI_IMAGE_TASK_PET"))
    val pet: Pet,

    @Column(name = "request_id", nullable = false, length = 255)
    @Comment("AI API 요청 ID")
    val requestId: String,

    @Column(name = "status", nullable = false, length = 20)
    @Comment("작업 상태 (PENDING, SUCCESS, FAIL)")
    var status: String,

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    @Comment("실패 사유")
    var failureReason: String? = null
) : BaseTimeEntity()
