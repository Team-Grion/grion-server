package com.project.grionserver.domain.user.repository

import com.project.grionserver.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByKakaoId(kakaoId: String): User?

    @Modifying(clearAutomatically = true)
    @Query(
        "UPDATE User u SET u.refreshToken = :newHash " +
            "WHERE u.id = :userId AND u.refreshToken = :oldHash"
    )
    fun updateRefreshTokenIfMatches(
        @Param("userId") userId: Long,
        @Param("oldHash") oldHash: String,
        @Param("newHash") newHash: String
    ): Int
}
