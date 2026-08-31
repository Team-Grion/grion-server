package com.project.grionserver.domain.auth.controller

import com.project.grionserver.domain.auth.dto.KakaoLoginRequest
import com.project.grionserver.domain.auth.dto.KakaoLoginResponse
import com.project.grionserver.domain.auth.dto.ReissueRequest
import com.project.grionserver.domain.auth.dto.ReissueResponse
import com.project.grionserver.domain.auth.service.AuthService
import com.project.grionserver.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "카카오 로그인", description = "JWT 액세스/리프레시 토큰을 발급합니다.")
    @SecurityRequirements
    @PostMapping("/kakao")
    fun loginWithKakao(
        @Valid @RequestBody request: KakaoLoginRequest
    ): ResponseEntity<ApiResponse<KakaoLoginResponse>> {
        val response = authService.loginWithKakao(request.accessToken)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새 액세스/리프레시 토큰을 발급합니다.")
    @SecurityRequirements
    @PostMapping("/reissue")
    fun reissue(
        @Valid @RequestBody request: ReissueRequest
    ): ResponseEntity<ApiResponse<ReissueResponse>> {
        val response = authService.reissue(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
