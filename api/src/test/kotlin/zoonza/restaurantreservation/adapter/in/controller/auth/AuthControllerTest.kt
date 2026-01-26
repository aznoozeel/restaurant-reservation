package zoonza.restaurantreservation.adapter.`in`.controller.auth

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import zoonza.restaurantreservation.adapter.out.persistence.customer.CustomerJpaRepository
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort
import zoonza.restaurantreservation.config.TestContainersConfiguration
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.SocialProvider
import zoonza.restaurantreservation.shared.UserRole

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Import(TestContainersConfiguration::class)
class AuthControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var tokenManagementPort: TokenManagementPort
    @Autowired lateinit var customerJpaRepository: CustomerJpaRepository

    @Value("\${jwt.access-token-expiry}")
    private var accessTokenExpiry: Long = 0

    @Value("\${jwt.refresh-token-expiry}")
    private var refreshTokenExpiry: Long = 0

    @Test
    fun `로그아웃 시 액세스 토큰을 블랙리스트에 추가하고 쿠키를 만료한다`() {
        val accessToken = tokenManagementPort.generateAccessToken(1L, UserRole.ROLE_CUSTOMER)
        val refreshToken = tokenManagementPort.generateRefreshToken(1L)

        mockMvc.post("/api/auth/logout") {
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
            )
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
            cookie {
                maxAge("accessToken", 0)
                maxAge("refreshToken", 0)
            }
        }
    }

    @Test
    fun `리프레시 토큰으로 새로운 토큰을 발급하고 쿠키를 갱신한다`() {
        val customer = customerJpaRepository.save(
            Customer.register(
                email = "refresh@example.com",
                nickname = "리프레시",
                provider = SocialProvider.GOOGLE,
                providerId = "provider-refresh"
            )
        )
        val refreshToken = tokenManagementPort.generateRefreshToken(customer.id)

        val result = mockMvc.post("/api/auth/refresh") {
            cookie(Cookie("refreshToken", refreshToken))
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
            cookie {
                maxAge("accessToken", (accessTokenExpiry / 1000).toInt())
                maxAge("refreshToken", (refreshTokenExpiry / 1000).toInt())
                httpOnly("accessToken", true)
                httpOnly("refreshToken", true)
                path("accessToken", "/")
                path("refreshToken", "/")
            }
        }.andReturn()

        val setCookieHeaders = result.response.getHeaders(HttpHeaders.SET_COOKIE)

        val accessTokenCookie = setCookieHeaders.first { it.startsWith("accessToken=") }
        val refreshTokenCookie = setCookieHeaders.first { it.startsWith("refreshToken=") }

        accessTokenCookie.shouldContain("SameSite=Lax")
        refreshTokenCookie.shouldContain("SameSite=Strict")
        accessTokenCookie.contains("accessToken=;") shouldBe false
        refreshTokenCookie.contains("refreshToken=;") shouldBe false
    }
}
