package zoonza.restaurantreservation.adapter.`in`.security.oauth2

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Component
class OAuth2FailureHandler(
    private val objectMapper: ObjectMapper
) : SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val status = resolveHttpStatus(exception)

        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.status = status.value()

        val problemDetail = createProblemDetail(exception, status)

        objectMapper.writeValue(response.writer, problemDetail)
    }

    private fun resolveHttpStatus(exception: AuthenticationException): HttpStatus {
        if (exception is OAuth2AuthenticationException) {
            return when (exception.error.errorCode) {
                "SUSPENDED", "WITHDRAWN" -> HttpStatus.FORBIDDEN
                else -> HttpStatus.UNAUTHORIZED
            }
        }

        return HttpStatus.UNAUTHORIZED
    }

    private fun createProblemDetail(exception: AuthenticationException, status: HttpStatus): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, exception.message)

        problemDetail.title = status.reasonPhrase
        problemDetail.setProperty("timestamp", LocalDateTime.now())

        return problemDetail
    }
}
