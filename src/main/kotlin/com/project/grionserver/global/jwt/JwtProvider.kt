package com.project.grionserver.global.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun createAccessToken(userId: Long): String = createToken(userId, accessTokenExpiration, "access")

    fun createRefreshToken(userId: Long): String = createToken(userId, refreshTokenExpiration, "refresh")

    private fun createToken(userId: Long, expiration: Long, type: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", type)
            .issuedAt(now)
            .expiration(Date(now.time + expiration))
            .signWith(key)
            .compact()
    }
}
