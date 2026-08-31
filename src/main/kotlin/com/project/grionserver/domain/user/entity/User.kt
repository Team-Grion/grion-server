package com.project.grionserver.domain.user.entity

import com.project.grionserver.domain.common.entity.BaseTimeEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "user")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("사용자 PK")
    val id: Long = 0L,

    @Column(name = "kakao_id", nullable = false, unique = true, length = 255)
    @Comment("카카오 고유 식별값")
    val kakaoId: String,

    @Column(name = "nickname", nullable = false, length = 50)
    @Comment("사용자 닉네임")
    var nickname: String,

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    @Comment("카카오 프로필 이미지 URL")
    var profileImageUrl: String? = null,

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    @Comment("발급된 리프레시 토큰")
    var refreshToken: String? = null
) : BaseTimeEntity()
