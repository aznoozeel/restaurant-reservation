package zoonza.restaurantreservation.auth.application.service

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort

class AuthServiceTest {
    private val tokenManagementPort = mockk<TokenManagementPort>()
    private val authService = AuthService(tokenManagementPort)

    @Test
    fun `로그아웃`() {
        val accessToken = "access-token"

        every {tokenManagementPort.addBlacklist(accessToken)} returns Unit
        every {tokenManagementPort.deleteRefreshToken(accessToken)} returns Unit

        authService.logout(accessToken)

        verify(exactly = 1) {
            tokenManagementPort.addBlacklist(accessToken)
            tokenManagementPort.deleteRefreshToken(accessToken)
        }
        confirmVerified(tokenManagementPort)
    }
}