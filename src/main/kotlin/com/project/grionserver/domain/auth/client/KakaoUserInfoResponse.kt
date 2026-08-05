package com.project.grionserver.domain.auth.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoUserInfoResponse(
    val id: Long,
    @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount?
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class KakaoAccount(
        val profile: KakaoProfile?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class KakaoProfile(
        val nickname: String?,
        @JsonProperty("profile_image_url") val profileImageUrl: String?
    )
}
