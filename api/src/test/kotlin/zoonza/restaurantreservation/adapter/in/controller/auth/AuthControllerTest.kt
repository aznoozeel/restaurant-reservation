package zoonza.restaurantreservation.adapter.`in`.controller.auth

import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import zoonza.restaurantreservation.auth.application.`in`.TokenManagementPort
import zoonza.restaurantreservation.config.TestContainersConfiguration
import zoonza.restaurantreservation.shared.UserRole

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Import(TestContainersConfiguration::class)
class AuthControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var tokenManagementPort: TokenManagementPort

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
}
