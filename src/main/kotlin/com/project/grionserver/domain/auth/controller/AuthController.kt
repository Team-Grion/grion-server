package com.project.grionserver.domain.auth.controller

import com.project.grionserver.domain.auth.dto.KakaoLoginRequest
import com.project.grionserver.domain.auth.dto.KakaoLoginResponse
import com.project.grionserver.domain.auth.service.AuthService
import com.project.grionserver.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
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
    @PostMapping("/kakao")
    fun loginWithKakao(
        @Valid @RequestBody request: KakaoLoginRequest
    ): ResponseEntity<ApiResponse<KakaoLoginResponse>> {
        val response = authService.loginWithKakao(request.accessToken)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
