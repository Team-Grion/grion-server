package com.project.grionserver.global.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.grionserver.global.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val message = request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_ATTRIBUTE) as? String
            ?: "인증이 필요합니다."

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                ApiResponse.fail(message, HttpStatus.UNAUTHORIZED.value())
            )
        )
    }
}
