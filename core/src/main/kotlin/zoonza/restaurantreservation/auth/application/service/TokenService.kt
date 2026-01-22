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
}