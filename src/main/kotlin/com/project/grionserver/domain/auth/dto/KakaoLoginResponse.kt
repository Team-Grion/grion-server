package com.project.grionserver.domain.auth.dto

data class KakaoLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long
)
