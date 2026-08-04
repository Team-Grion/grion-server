package com.project.grionserver.domain.user.controller

import com.project.grionserver.domain.user.dto.UserPageResponse
import com.project.grionserver.domain.user.service.UserService
import com.project.grionserver.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<ApiResponse<UserPageResponse>> {
        val response = userService.getMe(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
