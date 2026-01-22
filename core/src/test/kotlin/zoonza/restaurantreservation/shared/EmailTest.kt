package zoonza.restaurantreservation.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EmailTest {
    @Test
    fun `올바른 이메일 형식이면 Email이 생성된다`() {
        val email = Email("test@example.com")

        email.address shouldBe "test@example.com"
    }

    @Test
    fun `잘못된 이메일 형식이면 예외가 발생한다`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Email("invalid-email")
        }

        exception.message shouldBe "잘못된 이메일 형식입니다."
    }
}
