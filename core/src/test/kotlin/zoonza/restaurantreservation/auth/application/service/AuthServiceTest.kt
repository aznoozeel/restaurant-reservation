package zoonza.restaurantreservation.auth.application.service

import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort
import zoonza.restaurantreservation.customer.application.`in`.CustomerManagementPort
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.SocialProvider
import zoonza.restaurantreservation.shared.UserRole

class AuthServiceTest {
    private val tokenManagementPort = mockk<TokenManagementPort>()
    private val customerManagementPort = mockk<CustomerManagementPort>()
    private val authService = AuthService(tokenManagementPort, customerManagementPort)

    @Test
    fun `로그아웃`() {
        val accessToken = "access-token"

        every { tokenManagementPort.addBlacklist(accessToken) } returns Unit
        every { tokenManagementPort.deleteRefreshToken(accessToken) } returns Unit

        authService.logout(accessToken)

        verify(exactly = 1) {
            tokenManagementPort.addBlacklist(accessToken)
            tokenManagementPort.deleteRefreshToken(accessToken)
        }
        confirmVerified(tokenManagementPort, customerManagementPort)
    }

    @Test
    fun `리프레시 토큰으로 새로운 토큰을 발급한다`() {
        val refreshToken = "refresh-token"
        val userId = 0L
        val customer = Customer.register(
            email = "test@example.com",
            nickname = "고객",
            provider = SocialProvider.GOOGLE,
            providerId = "provider-1234"
        )

        every { tokenManagementPort.extractUserId(refreshToken) } returns userId
        every { customerManagementPort.find(userId) } returns customer
        every { tokenManagementPort.generateAccessToken(userId, UserRole.ROLE_CUSTOMER) } returns "new-access-token"
        every { tokenManagementPort.generateRefreshToken(userId) } returns "new-refresh-token"

        val result = authService.refreshToken(refreshToken)

        result.accessToken shouldBe "new-access-token"
        result.refreshToken shouldBe "new-refresh-token"

        verify(exactly = 1) {
            tokenManagementPort.extractUserId(refreshToken)
            customerManagementPort.find(userId)
            tokenManagementPort.generateAccessToken(userId, UserRole.ROLE_CUSTOMER)
            tokenManagementPort.generateRefreshToken(userId)
        }
        confirmVerified(tokenManagementPort, customerManagementPort)
    }
}
