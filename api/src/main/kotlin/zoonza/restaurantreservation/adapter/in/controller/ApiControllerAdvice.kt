package zoonza.restaurantreservation.adapter.`in`.controller

import org.springframework.http.*
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import zoonza.restaurantreservation.common.BusinessException
import zoonza.restaurantreservation.common.ErrorCode
import java.time.LocalDateTime

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {
    private val statusMap = mapOf(
        ErrorCode.BLACKLISTED to HttpStatus.FORBIDDEN,
        ErrorCode.NOT_FOUND to HttpStatus.NOT_FOUND,
        )

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, "입력 값이 유효하지 않습니다.")

        problemDetail.setProperty("exception", ex.javaClass.simpleName)
        problemDetail.setProperty("timestamp", LocalDateTime.now())

        val errors = ex.bindingResult.fieldErrors.map {
            mapOf(
                "field" to it.field,
                "message" to it.defaultMessage
            )
        }
        problemDetail.setProperty("errors", errors)

        return ResponseEntity(problemDetail, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception) =
        getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException) =
        getProblemDetail(statusMap[exception.errorCode]!!, exception)

    private fun getProblemDetail(status: HttpStatus, exception: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, exception.message)

        problemDetail.title = status.reasonPhrase
        problemDetail.setProperty("timestamp", LocalDateTime.now())

        return problemDetail
    }
}