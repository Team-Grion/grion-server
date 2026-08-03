package com.project.grionserver.domain.auth.service

import com.project.grionserver.domain.auth.client.KakaoAuthClient
import com.project.grionserver.domain.auth.dto.KakaoLoginResponse
import com.project.grionserver.domain.user.entity.User
import com.project.grionserver.domain.user.repository.UserRepository
import com.project.grionserver.global.jwt.JwtProvider
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
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

        return KakaoLoginResponse(
            accessToken = jwtProvider.createAccessToken(user.id),
            refreshToken = jwtProvider.createRefreshToken(user.id),
            userId = user.id
        )
    }
}
