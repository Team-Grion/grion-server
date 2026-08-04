package com.project.grionserver.global.jwt

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.removePrefix("Bearer ")
            try {
                val userId = jwtProvider.getUserIdFromAccessToken(token)
                val authentication = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: JwtException) {
                SecurityContextHolder.clearContext()
                request.setAttribute(AUTH_ERROR_ATTRIBUTE, "유효하지 않은 엑세스 토큰입니다.")
            } catch (e: IllegalArgumentException) {
                SecurityContextHolder.clearContext()
                request.setAttribute(AUTH_ERROR_ATTRIBUTE, "유효하지 않은 엑세스 토큰입니다.")
            }
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val AUTH_ERROR_ATTRIBUTE = "authError"
    }
}
