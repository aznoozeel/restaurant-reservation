package zoonza.restaurantreservation.`in`.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import zoonza.restaurantreservation.auth.application.service.BlacklistedException
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Component
class JwtExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.requestURI.startsWith("/login/oauth2/code")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            when (e) {
                is ExpiredTokenException, is BlacklistedException -> sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.message)
                is JwtException -> sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.")
            }
        }
    }

    private fun sendErrorResponse(
        response: HttpServletResponse,
        status: HttpStatus,
        message: String?
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val problemDetail = ProblemDetail.forStatusAndDetail(status, message)

        problemDetail.title = status.reasonPhrase
        problemDetail.setProperty("timestamp", LocalDateTime.now())

        objectMapper.writeValue(response.writer, problemDetail)
    }
}