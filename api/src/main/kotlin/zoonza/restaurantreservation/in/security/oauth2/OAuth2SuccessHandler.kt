package zoonza.restaurantreservation.`in`.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort
import java.time.Duration

@Component
class OAuth2SuccessHandler(
    @Value("\${jwt.access-token-expiry}")
    private val accessTokenExpiry: Long,

    @Value("\${jwt.refresh-token-expiry}")
    private val refreshTokenExpiry: Long,

    private val tokenManagementPort: TokenManagementPort
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val customOAuth2User = authentication.principal as CustomOAuth2User
        val customer = customOAuth2User.customer

        val accessToken = tokenManagementPort.generateAccessToken(customer.id, customer.role)
        val refreshToken = tokenManagementPort.generateRefreshToken(customer.id)

        setCookie(response,"accessToken", accessToken, accessTokenExpiry, "Lax")
        setCookie(response,"refreshToken", refreshToken, refreshTokenExpiry, "Strict")

        response.sendRedirect("http://localhost:3000/home")
    }

    private fun setCookie(
        response: HttpServletResponse,
        name: String,
        value: String,
        maxAge: Long,
        sameSite: String
    ) {
        val cookie = createCookie(name, value, maxAge, sameSite)

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    private fun createCookie(name: String, value: String, maxAge: Long, sameSite: String) =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofMinutes(maxAge))
            .sameSite(sameSite)
            .build()
}