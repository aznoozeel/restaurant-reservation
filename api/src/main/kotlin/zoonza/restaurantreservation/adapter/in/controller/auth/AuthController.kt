package zoonza.restaurantreservation.adapter.`in`.controller.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zoonza.restaurantreservation.adapter.`in`.controller.ApiResponse
import zoonza.restaurantreservation.auth.application.`in`.AuthenticationPort
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    @Value("\${jwt.access-token-expiry}")
    private val accessTokenExpiry: Long,

    @Value("\${jwt.refresh-token-expiry}")
    private val refreshTokenExpiry: Long,

    private val authenticationPort: AuthenticationPort
) {
    @PostMapping("/logout")
    fun logout(@CookieValue("accessToken") accessToken: String): ResponseEntity<ApiResponse<Any>> {
        authenticationPort.logout(accessToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, expireCookie("accessToken").toString())
            .header(HttpHeaders.SET_COOKIE, expireCookie("refreshToken").toString())
            .body(ApiResponse.success())
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue("refreshToken") refreshToken: String
    ): ResponseEntity<ApiResponse<Any>> {
        val result = authenticationPort.refreshToken(refreshToken)

        val accessTokenCookie = createCookie("accessToken", result.accessToken, accessTokenExpiry, "Lax")
        val refreshTokenCookie = createCookie("refreshToken", result.refreshToken, refreshTokenExpiry, "Strict")

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponse.success())
    }

    fun expireCookie(name: String): ResponseCookie {
        return ResponseCookie.from(name, "")
            .path("/")
            .maxAge(Duration.ZERO)
            .build()
    }

    fun createCookie(name: String, value: String, maxAge: Long, sameSite: String): ResponseCookie {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofMillis(maxAge))
            .sameSite(sameSite)
            .build()
    }
}