package zoonza.restaurantreservation.auth.application.service

import org.springframework.stereotype.Service
import zoonza.restaurantreservation.auth.application.`in`.AuthenticationPort
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort

@Service
class AuthService(
    private val tokenManagementPort: TokenManagementPort
): AuthenticationPort {
    override fun logout(token: String) {
        tokenManagementPort.addBlacklist(token)
        tokenManagementPort.deleteRefreshToken(token)
    }
}