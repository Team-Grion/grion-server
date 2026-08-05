package com.project.grionserver.domain.user.controller

import com.project.grionserver.domain.user.dto.UserPageResponse
import com.project.grionserver.domain.user.service.UserService
import com.project.grionserver.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @Operation(summary = "마이페이지 조회", description = "로그인한 유저의 프로필 정보와 본인이 보낸 편지 목록을 조회합니다.")
    @GetMapping("/me")
    fun getMyPage(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<ApiResponse<UserPageResponse>> {
        val response = userService.getMyPage(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
