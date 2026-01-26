package zoonza.restaurantreservation.auth.application.service

import org.springframework.stereotype.Service
import zoonza.restaurantreservation.auth.application.`in`.AuthenticationPort
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort
import zoonza.restaurantreservation.auth.application.service.dto.result.RefreshTokenResult
import zoonza.restaurantreservation.customer.application.`in`.CustomerManagementPort

@Service
class AuthService(
    private val tokenManagementPort: TokenManagementPort,
    private val customerManagementPort: CustomerManagementPort
): AuthenticationPort {
    override fun logout(token: String) {
        tokenManagementPort.addBlacklist(token)
        tokenManagementPort.deleteRefreshToken(token)
    }

    override fun refreshToken(refreshToken: String): RefreshTokenResult {
        val userId = tokenManagementPort.extractUserId(refreshToken)

        val customer = customerManagementPort.find(userId)

        val newAccessToken = tokenManagementPort.generateAccessToken(customer.id, customer.role)
        val newRefreshToken = tokenManagementPort.generateRefreshToken(customer.id)

        return RefreshTokenResult(newAccessToken, newRefreshToken)
    }
}