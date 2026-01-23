package zoonza.restaurantreservation.out.persistence.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import zoonza.restaurantreservation.auth.application.out.TokenRepository
import java.util.concurrent.TimeUnit

@Repository
class TokenRepositoryImpl(
    @Value("\${jwt.refresh-token-expiry}")
    private val refreshTokenExpiry: Long,

    private val stringRedisTemplate: StringRedisTemplate,
) : TokenRepository {
    companion object {
        private const val REFRESH_TOKEN_KEY_PREFIX = "RT:"
        private const val BLACKLIST_KEY_PREFIX = "BLACKLIST:"
    }

    override fun saveRefreshToken(userId: Long, refreshToken: String) {
        stringRedisTemplate.opsForValue().set(
            getRefreshTokenKey(userId),
            refreshToken,
            refreshTokenExpiry,
            TimeUnit.MILLISECONDS
        )
    }

    override fun addBlacklist(jti: String, remainingTime: Long) {
        stringRedisTemplate.opsForValue().set(
            getBlacklistKey(jti),
            "",
            remainingTime,
            TimeUnit.MILLISECONDS
        )
    }

    override fun existsInBlacklist(jti: String): Boolean {
        return stringRedisTemplate.hasKey(getBlacklistKey(jti))
    }

    private fun getRefreshTokenKey(userId: Long): String =
        "${REFRESH_TOKEN_KEY_PREFIX}$userId"

    private fun getBlacklistKey(jti: String): String =
        "${BLACKLIST_KEY_PREFIX}$jti"
}