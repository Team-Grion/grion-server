package com.project.grionserver.domain.user.repository

import com.project.grionserver.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByKakaoId(kakaoId: String): User?
}
