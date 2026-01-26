package zoonza.restaurantreservation.auth.application.service.dto.result

data class RefreshTokenResult(
    val accessToken: String,
    val refreshToken: String
)
