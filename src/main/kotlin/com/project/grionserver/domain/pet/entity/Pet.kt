package com.project.grionserver.domain.pet.entity

import com.project.grionserver.domain.common.entity.BaseTimeEntity
import com.project.grionserver.domain.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
@Table(name = "pet")
class Pet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("반려동물 PK")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "FK_PET_USER"))
    val user: User,

    @Column(name = "name", length = 50)
    @Comment("반려동물 이름")
    var name: String? = null,

    @Column(name = "birth_date")
    @Comment("반려동물 생일")
    var birthday: LocalDate? = null,

    @Column(name = "death_date")
    @Comment("이별한 날짜")
    var deathDate: LocalDate? = null,

    @Column(name = "species", nullable = false, length = 20)
    @Comment("반려동물 종 (강아지, 고양이 등)")
    var species: String,

    @Column(name = "breed", nullable = false, length = 50)
    @Comment("상세 품종")
    var breed: String,

    @ElementCollection
    @CollectionTable(
        name = "pet_personality",
        joinColumns = [JoinColumn(name = "pet_id", foreignKey = ForeignKey(name = "FK_PET_PERSONALITY_PET"))]
    )
    @Column(name = "personality", nullable = false, length = 50)
    var personalities: MutableList<String> = mutableListOf(),

    @Column(name = "background_text", length = 255)
    @Comment("추모 페이지 배경 설명")
    var backgroundText: String? = null,

    @Column(name = "memories", columnDefinition = "TEXT")
    @Comment("반려동물과의 추억 기록")
    var memories: String? = null,

    @Column(name = "is_shared", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Comment("공개 여부")
    var isShared: Boolean = false
) : BaseTimeEntity()
