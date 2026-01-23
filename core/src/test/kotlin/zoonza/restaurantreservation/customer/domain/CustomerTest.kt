package zoonza.restaurantreservation.customer.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import zoonza.restaurantreservation.shared.LoginStatus
import zoonza.restaurantreservation.shared.UserRole

class CustomerTest {
    @Test
    fun `고객을 등록 성공`() {
        val customer = Customer.register(
            email = "test@example.com",
            nickname = "테스터",
            provider = SocialProvider.GOOGLE,
            providerId = "provider-1234"
        )

        customer.email.address shouldBe "test@example.com"
        customer.nickname shouldBe "테스터"
        customer.provider shouldBe SocialProvider.GOOGLE
        customer.providerId shouldBe "provider-1234"
        customer.status shouldBe CustomerStatus.ACTIVE
        customer.role shouldBe UserRole.ROLE_CUSTOMER
    }

    @Test
    fun `이메일이 비어 있으면 등록에 실패한다`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Customer.register(
                email = " ",
                nickname = "테스터",
                provider = SocialProvider.GOOGLE,
                providerId = "provider-1234"
            )
        }

        exception.message shouldBe "이메일은 필수입니다."
    }

    @Test
    fun `닉네임이 비어 있으면 등록에 실패한다`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Customer.register(
                email = "test@example.com",
                nickname = " ",
                provider = SocialProvider.GOOGLE,
                providerId = "provider-1234"
            )
        }

        exception.message shouldBe "닉네임은 필수입니다."
    }

    @Test
    fun `OAuth2 제공자 ID가 비어 있으면 등록에 실패한다`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Customer.register(
                email = "test@example.com",
                nickname = "테스터",
                provider = SocialProvider.GOOGLE,
                providerId = " "
            )
        }

        exception.message shouldBe "OAuth2 제공자 ID는 필수입니다."
    }

    @Test
    fun `상태에 따라 로그인 가능 여부가 결정된다`() {
        val customer = Customer.register(
            email = "test@example.com",
            nickname = "테스터",
            provider = SocialProvider.GOOGLE,
            providerId = "provider-1234"
        )

        customer.checkLoginAvailability() shouldBe LoginStatus.AVAILABLE

        customer.status = CustomerStatus.SUSPENDED
        customer.checkLoginAvailability() shouldBe LoginStatus.SUSPENDED

        customer.status = CustomerStatus.WITHDRAWN
        customer.checkLoginAvailability() shouldBe LoginStatus.WITHDRAWN
    }

    @Test
    fun `마지막 로그인 시간을 업데이트 한다`() {
        val customer = Customer.register(
            email = "test@example.com",
            nickname = "테스터",
            provider = SocialProvider.GOOGLE,
            providerId = "provider-1234"
        )

        customer.lastLoginAt shouldBe null

        customer.updateLastLoginAt()

        customer.lastLoginAt shouldNotBe null
    }
}
