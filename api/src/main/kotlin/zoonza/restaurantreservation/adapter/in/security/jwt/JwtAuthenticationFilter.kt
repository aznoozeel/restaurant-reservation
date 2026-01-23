package zoonza.restaurantreservation.`in`.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils
import zoonza.restaurantreservation.auth.application.`in`.TokenBlacklistPort
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort

@Component
class JwtAuthenticationFilter(
    private val tokenBlacklistPort: TokenBlacklistPort,
    private val tokenManagementPort: TokenManagementPort
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getTokenFromCookie(request)

        if (!token.isNullOrBlank()) {
            tokenBlacklistPort.checkBlacklist(token)

            tokenManagementPort.validate(token)

            val authentication = getAuthentication(token)

            SecurityContextHolder.getContext().authentication = authentication
        }
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val userId = tokenManagementPort.extractUserId(token)
        val userRole = tokenManagementPort.extractUserRole(token)

        return UsernamePasswordAuthenticationToken(
            userId,
            null,
            listOf(SimpleGrantedAuthority(userRole))
        )
    }

    private fun getTokenFromCookie(request: HttpServletRequest): String? {
        return WebUtils.getCookie(request, "accessToken")?.value
    }
}