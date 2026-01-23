package zoonza.restaurantreservation.`in`.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import zoonza.restaurantreservation.auth.application.out.TokenProvider
import zoonza.restaurantreservation.shared.UserRole
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.access-token-expiry}")
    private val accessTokenExpiry: Long,

    @Value("\${jwt.refresh-token-expiry}")
    private val refreshTokenExpiry: Long,
) : TokenProvider {
    override fun generateAccessToken(userId: Long, role: UserRole): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(userId.toString())
            .claim("role", role.name)
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(accessTokenExpiry).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun generateRefreshToken(userId: Long): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(userId.toString())
            .claim("jti", UUID.randomUUID().toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(refreshTokenExpiry).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun validateToken(token: String) {
        try{
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            throw ExpiredTokenException()
        } catch (e: JwtException) {
            throw InvalidTokenException()
        }
    }

    override fun getUserId(token: String): Long {
        val claims = parseClaims(token)
        return claims.subject.toLong()
    }

    override fun getUserRole(token: String): String {
        val claims = parseClaims(token)
        return claims["role"] as String
    }

    override fun getJti(token: String): String {
        val claims = parseClaims(token)
        return claims["jti"] as String
    }

    override fun getRemainingTime(token: String): Long {
        val expiration = parseClaims(token).expiration
        val now = Date()

        return maxOf(0, expiration.time - now.time)
    }

    private fun getKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}