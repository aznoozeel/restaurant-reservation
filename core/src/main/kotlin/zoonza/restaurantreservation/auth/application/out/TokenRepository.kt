package zoonza.restaurantreservation.auth.application.out

interface TokenRepository {
    fun saveRefreshToken(userId: Long, refreshToken: String)

    fun addBlacklist(jti: String, remainingTime: Long)

    fun existsInBlacklist(jti: String): Boolean

    fun deleteRefreshTokenByUserId(userId: Long)
}