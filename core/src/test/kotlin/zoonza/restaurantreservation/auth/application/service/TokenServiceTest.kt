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

    @Test
    fun `블랙리스트에 없으면 예외가 발생하지 않는다`() {
        every { tokenProvider.getJti("token") } returns "jti-123"
        every { tokenRepository.existsInBlacklist("jti-123") } returns false

        tokenService.checkBlacklist("token")

        verify(exactly = 1) { tokenProvider.getJti("token") }
        verify(exactly = 1) { tokenRepository.existsInBlacklist("jti-123") }
        confirmVerified(tokenRepository, tokenProvider)
    }

    @Test
    fun `블랙리스트에 있으면 예외가 발생한다`() {
        every { tokenProvider.getJti("token") } returns "jti-123"
        every { tokenRepository.existsInBlacklist("jti-123") } returns true

        val exception = shouldThrow<BlacklistedException> {
            tokenService.checkBlacklist("token")
        }

        exception.message shouldBe "블랙리스트에 등록된 토큰입니다."

        verify(exactly = 1) { tokenProvider.getJti("token") }
        verify(exactly = 1) { tokenRepository.existsInBlacklist("jti-123") }
        confirmVerified(tokenRepository, tokenProvider)
    }

    @Test
    fun `블랙리스트에 추가한다`() {
        val token = "access-token"
        val remainingTime = 1_000L
        val jti = "jti-123"

        every { tokenProvider.getRemainingTime(token) } returns remainingTime
        every { tokenProvider.getJti(token) } returns jti
        every { tokenRepository.addBlacklist(jti, remainingTime) } returns Unit

        tokenService.addBlacklist(token)

        verify(exactly = 1) {
            tokenProvider.getRemainingTime(token)
            tokenProvider.getJti(token)
            tokenRepository.addBlacklist(jti, remainingTime)
        }
        confirmVerified(tokenProvider, tokenRepository)
    }

    @Test
    fun `리프레시 토큰을 삭제한다`() {
        val token = "access-token"
        val userId = 1L

        every { tokenProvider.getUserId(token) } returns userId
        every { tokenRepository.deleteRefreshTokenByUserId(userId) } returns Unit

        tokenService.deleteRefreshToken(token)

        verify(exactly = 1) {
            tokenProvider.getUserId(token)
            tokenRepository.deleteRefreshTokenByUserId(userId)
        }
        confirmVerified(tokenProvider, tokenRepository)
    }
}
