package com.project.grionserver.domain.auth.client

import com.project.grionserver.global.exception.UnauthorizedException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class KakaoAuthClient {
    private val restTemplate = RestTemplate()

    fun getUserInfo(kakaoAccessToken: String): KakaoUserInfoResponse {
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $kakaoAccessToken")
        }

        return try {
            restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                HttpEntity<Void>(headers),
                KakaoUserInfoResponse::class.java
            ).body ?: throw UnauthorizedException("카카오 사용자 정보를 가져올 수 없습니다.")
        } catch (e: RestClientException) {
            throw UnauthorizedException("유효하지 않은 카카오 액세스 토큰입니다.")
        }
    }
}
