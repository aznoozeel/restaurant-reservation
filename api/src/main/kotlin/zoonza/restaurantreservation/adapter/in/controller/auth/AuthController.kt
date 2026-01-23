package zoonza.restaurantreservation.adapter.`in`.controller.auth

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

    fun expireCookie(name: String): ResponseCookie {
        return ResponseCookie.from(name, "")
            .path("/")
            .maxAge(Duration.ZERO)
            .build()
    }
}