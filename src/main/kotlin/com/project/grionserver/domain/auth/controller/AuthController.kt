package com.project.grionserver.domain.auth.controller

import com.project.grionserver.domain.auth.dto.KakaoLoginRequest
import com.project.grionserver.domain.auth.dto.KakaoLoginResponse
import com.project.grionserver.domain.auth.service.AuthService
import com.project.grionserver.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/kakao")
    fun loginWithKakao(
        @Valid @RequestBody request: KakaoLoginRequest
    ): ResponseEntity<ApiResponse<KakaoLoginResponse>> {
        val response = authService.loginWithKakao(request.accessToken)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
