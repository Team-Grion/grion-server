package com.project.grionserver.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class KakaoLoginRequest(
    @field:NotBlank(message = "accessToken은 필수입니다.")
    val accessToken: String
)
