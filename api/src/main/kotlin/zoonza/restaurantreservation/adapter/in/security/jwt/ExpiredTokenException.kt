package zoonza.restaurantreservation.adapter.`in`.security.jwt

class ExpiredTokenException(
    message: String = "토큰이 만료됐습니다."
) : RuntimeException(message)