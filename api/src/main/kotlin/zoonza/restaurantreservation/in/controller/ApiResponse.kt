package zoonza.restaurantreservation.`in`.controller

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> {
            return ApiResponse(data = data)
        }

        fun success(): ApiResponse<Any> {
            return ApiResponse()
        }
    }
}