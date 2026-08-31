package com.project.grionserver.domain.auth.service

import com.project.grionserver.domain.auth.client.KakaoAuthClient
import com.project.grionserver.domain.auth.dto.KakaoLoginResponse
import com.project.grionserver.domain.auth.dto.ReissueResponse
import com.project.grionserver.domain.user.entity.User
import com.project.grionserver.domain.user.repository.UserRepository
import com.project.grionserver.global.exception.UnauthorizedException
import com.project.grionserver.global.jwt.JwtProvider
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest

@Service
@Transactional
class AuthService(
    private val kakaoAuthClient: KakaoAuthClient,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider
) {
    fun loginWithKakao(kakaoAccessToken: String): KakaoLoginResponse {
        val kakaoUserInfo = kakaoAuthClient.getUserInfo(kakaoAccessToken)
        val kakaoId = kakaoUserInfo.id.toString()
        val profile = kakaoUserInfo.kakaoAccount?.profile

        val user = userRepository.findByKakaoId(kakaoId)
            ?: try {
                userRepository.save(
                    User(
                        kakaoId = kakaoId,
                        nickname = profile?.nickname ?: "사용자",
                        profileImageUrl = profile?.profileImageUrl
                    )
                )
            } catch (e: DataIntegrityViolationException) {
                userRepository.findByKakaoId(kakaoId) ?: throw e
            }

        val accessToken = jwtProvider.createAccessToken(user.id)
        val refreshToken = jwtProvider.createRefreshToken(user.id)
        user.refreshToken = hashToken(refreshToken)
        userRepository.save(user)

        return KakaoLoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = user.id
        )
    }

    fun reissue(refreshToken: String): ReissueResponse {
        val userId = jwtProvider.getUserIdFromRefreshToken(refreshToken)

        val user = userRepository.findById(userId)
            .orElseThrow { UnauthorizedException("리프레시 토큰의 유저 정보가 올바르지 않습니다.") }

        if (user.refreshToken != hashToken(refreshToken)) {
            throw UnauthorizedException("리프레시 토큰 정보가 일치하지 않습니다.")
        }

        val newAccessToken = jwtProvider.createAccessToken(user.id)
        val newRefreshToken = jwtProvider.createRefreshToken(user.id)
        user.refreshToken = hashToken(newRefreshToken)
        userRepository.save(user)

        return ReissueResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(token.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
