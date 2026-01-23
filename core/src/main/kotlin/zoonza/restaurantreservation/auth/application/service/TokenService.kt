package zoonza.restaurantreservation.auth.application.service

import org.springframework.stereotype.Service
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort
import zoonza.restaurantreservation.auth.application.out.TokenProvider
import zoonza.restaurantreservation.auth.application.out.TokenRepository
import zoonza.restaurantreservation.shared.UserRole

@Service
class TokenService(
    private val tokenProvider: TokenProvider,
    private val tokenRepository: TokenRepository
) : TokenManagementPort {
    override fun generateAccessToken(userId: Long, role: UserRole): String {
        return tokenProvider.generateAccessToken(userId, role)
    }

    override fun generateRefreshToken(userId: Long): String {
        return tokenProvider.generateRefreshToken(userId)
            .also { refreshToken -> tokenRepository.saveRefreshToken(userId, refreshToken) }
    }

    override fun validate(token: String) {
        tokenProvider.validateToken(token)
    }

    override fun extractUserId(token: String): Long {
        return tokenProvider.getUserId(token)
    }

    override fun extractUserRole(token: String): String {
        return tokenProvider.getUserRole(token)
    }

    override fun deleteRefreshToken(token: String) {
        val userId = tokenProvider.getUserId(token)

        tokenRepository.deleteRefreshTokenByUserId(userId)
    }

    override fun checkBlacklist(token: String) {
        val jti = tokenProvider.getJti(token)

        if (tokenRepository.existsInBlacklist(jti)) {
            throw BlacklistedException()
        }
    }

    override fun addBlacklist(token: String) {
        val remainingTime = tokenProvider.getRemainingTime(token)

        if (remainingTime <= 0L) return

        tokenRepository.addBlacklist(tokenProvider.getJti(token), remainingTime)
    }
}