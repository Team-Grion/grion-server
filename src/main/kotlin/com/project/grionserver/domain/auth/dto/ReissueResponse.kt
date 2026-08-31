package com.project.grionserver.domain.auth.dto

data class ReissueResponse(
    val accessToken: String,
    val refreshToken: String
)
