package zoonza.restaurantreservation.auth.application.service

import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import zoonza.restaurantreservation.auth.application.out.TokenProvider
import zoonza.restaurantreservation.auth.application.out.TokenRepository
import zoonza.restaurantreservation.shared.UserRole

class TokenServiceTest {
    private val tokenProvider = mockk<TokenProvider>()
    private val tokenRepository = mockk<TokenRepository>()
    private val tokenService = TokenService(tokenProvider, tokenRepository)

    @Test
    fun `액세스 토큰 생성`() {
        every { tokenProvider.generateAccessToken(10L, UserRole.ROLE_CUSTOMER) } returns "access-token"

        val result = tokenService.generateAccessToken(10L, UserRole.ROLE_CUSTOMER)

        result shouldBe "access-token"

        verify(exactly = 1) { tokenProvider.generateAccessToken(10L, UserRole.ROLE_CUSTOMER) }
        confirmVerified(tokenProvider, tokenRepository)
    }

    @Test
    fun `리프레시 토큰 생성 및 저장`() {
        every { tokenProvider.generateRefreshToken(10L) } returns "refresh-token"
        every { tokenRepository.saveRefreshToken(10L, "refresh-token") } returns Unit

        val result = tokenService.generateRefreshToken(10L)

        result shouldBe "refresh-token"

        verify(exactly = 1) { tokenProvider.generateRefreshToken(10L) }
        verify(exactly = 1) { tokenRepository.saveRefreshToken(10L, "refresh-token") }
        confirmVerified(tokenProvider, tokenRepository)
    }

    @Test
    fun `토큰 검증`() {
        every { tokenProvider.validateToken("token") } returns Unit

        tokenService.validate("token")

        verify(exactly = 1) { tokenProvider.validateToken("token") }
        confirmVerified(tokenProvider, tokenRepository)
    }

    @Test
    fun `토큰에서 사용자 ID를 추출한다`() {
        every { tokenProvider.getUserId("token") } returns 10L

        val result = tokenService.extractUserId("token")

        result shouldBe 10L

        verify(exactly = 1) { tokenProvider.getUserId("token") }
        confirmVerified(tokenProvider, tokenRepository)
    }

    @Test
    fun `토큰에서 사용자 ROLE을 추출한다`() {
        every { tokenProvider.getUserRole("token") } returns "ROLE_CUSTOMER"

        val result = tokenService.extractUserRole("token")

        result shouldBe "ROLE_CUSTOMER"

        verify(exactly = 1) { tokenProvider.getUserRole("token") }
        confirmVerified(tokenProvider, tokenRepository)
    }
}
