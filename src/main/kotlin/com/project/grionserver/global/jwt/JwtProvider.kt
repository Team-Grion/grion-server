package com.project.grionserver.global.jwt

import com.project.grionserver.global.exception.UnauthorizedException
import io.jsonwebtoken.JwtException
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

    fun getUserIdFromAccessToken(token: String): Long {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        require(claims["type"] == "access") { "엑세스 토큰이 아닙니다." }
        return claims.subject.toLong()
    }

    fun getUserIdFromRefreshToken(token: String): Long {
        val claims = try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (e: JwtException) {
            throw UnauthorizedException("유효하지 않은 리프레시 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            throw UnauthorizedException("유효하지 않은 리프레시 토큰입니다.")
        }

        if (claims["type"] != "refresh") {
            throw UnauthorizedException("리프레시 토큰의 타입이 올바르지 않습니다.")
        }

        return claims.subject?.toLongOrNull()
            ?: throw UnauthorizedException("리프레시 토큰의 유저 정보가 올바르지 않습니다.")
    }

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
