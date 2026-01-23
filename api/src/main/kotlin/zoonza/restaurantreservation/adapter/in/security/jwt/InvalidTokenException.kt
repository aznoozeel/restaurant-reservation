package zoonza.restaurantreservation.adapter.`in`.security.jwt

class InvalidTokenException(
    message: String = "유효하지 않은 토큰입니다."
) : RuntimeException(message)