package com.project.grionserver.domain.user.repository

import com.project.grionserver.domain.user.entity.User
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByKakaoId(kakaoId: String): User?

    // 락 조회 (추모 공간 생성 개수 제한 검증용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    fun findByIdForUpdate(@Param("id") id: Long): User?

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
