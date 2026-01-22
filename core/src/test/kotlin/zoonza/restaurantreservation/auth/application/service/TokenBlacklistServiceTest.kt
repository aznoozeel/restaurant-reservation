package zoonza.restaurantreservation.auth.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import zoonza.restaurantreservation.auth.application.out.TokenProvider
import zoonza.restaurantreservation.auth.application.out.TokenRepository

class TokenBlacklistServiceTest {
    private val tokenRepository = mockk<TokenRepository>()
    private val tokenProvider = mockk<TokenProvider>()
    private val tokenBlacklistService = TokenBlacklistService(tokenRepository, tokenProvider)

    @Test
    fun `블랙리스트에 없으면 예외가 발생하지 않는다`() {
        every { tokenProvider.getJti("token") } returns "jti-123"
        every { tokenRepository.existsInBlacklist("jti-123") } returns false

        tokenBlacklistService.checkBlacklist("token")

        verify(exactly = 1) { tokenProvider.getJti("token") }
        verify(exactly = 1) { tokenRepository.existsInBlacklist("jti-123") }
        confirmVerified(tokenRepository, tokenProvider)
    }

    @Test
    fun `블랙리스트에 있으면 예외가 발생한다`() {
        every { tokenProvider.getJti("token") } returns "jti-123"
        every { tokenRepository.existsInBlacklist("jti-123") } returns true

        val exception = shouldThrow<BlacklistedException> {
            tokenBlacklistService.checkBlacklist("token")
        }

        exception.message shouldBe "블랙리스트에 등록된 토큰입니다."

        verify(exactly = 1) { tokenProvider.getJti("token") }
        verify(exactly = 1) { tokenRepository.existsInBlacklist("jti-123") }
        confirmVerified(tokenRepository, tokenProvider)
    }
}
