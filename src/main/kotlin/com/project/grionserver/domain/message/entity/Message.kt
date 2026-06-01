package com.project.grionserver.domain.message.entity

import com.project.grionserver.domain.common.entity.BaseCreatedAtEntity
import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "message")
class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("메시지 PK")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, foreignKey = ForeignKey(name = "FK_MESSAGE_PET"))
    val pet: Pet,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false, foreignKey = ForeignKey(name = "FK_MESSAGE_USER"))
    val sender: User,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Comment("메시지 본문")
    var content: String,

    @Column(name = "is_anonymous", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Comment("익명 여부")
    var isAnonymous: Boolean = false,

    @Column(name = "status", nullable = false, length = 20)
    @Comment("메시지 상태")
    var status: String
) : BaseCreatedAtEntity()
