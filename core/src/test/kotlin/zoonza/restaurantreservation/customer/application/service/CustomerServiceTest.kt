package zoonza.restaurantreservation.customer.application.service

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import zoonza.restaurantreservation.customer.application.out.CustomerRepository
import zoonza.restaurantreservation.customer.application.service.command.FindOrCreateCustomerCommand
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.CustomerStatus
import zoonza.restaurantreservation.customer.domain.SocialProvider
import zoonza.restaurantreservation.shared.UserRole
import java.time.LocalDateTime

class CustomerServiceTest {
    private val customerRepository = mockk<CustomerRepository>()
    private val customerService = CustomerService(customerRepository)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `기존 고객이 있으면 저장하지 않고 그대로 반환한다`() {
        val existingCustomer = Customer.register(
            email = "test@example.com",
            nickname = "기존고객",
            provider = SocialProvider.GOOGLE,
            providerId = "provider-1234"
        )

        every {
            customerRepository.findByProviderAndProviderId(SocialProvider.GOOGLE, "provider-1234")
        } returns existingCustomer

        val result = customerService.findOrCreate(
            FindOrCreateCustomerCommand(
                email = "test@example.com",
                provider = SocialProvider.GOOGLE,
                providerId = "provider-1234"
            )
        )

        result shouldBe existingCustomer

        verify(exactly = 1) { customerRepository.findByProviderAndProviderId(SocialProvider.GOOGLE, "provider-1234") }
        verify(exactly = 0) { customerRepository.save(any()) }
        verify(exactly = 0) { customerRepository.existsByNickname(any()) }
        confirmVerified(customerRepository)
    }

    @Test
    fun `신규 고객이면 닉네임 중복을 피해서 저장한다`() {
        mockkObject(RandomNicknameGenerator)

        every {
            customerRepository.findByProviderAndProviderId(SocialProvider.GOOGLE, "provider-1234")
        } returns null
        every { RandomNicknameGenerator.generate() } returnsMany listOf("충돌닉네임", "신규닉네임")
        every { customerRepository.existsByNickname("충돌닉네임") } returns true
        every { customerRepository.existsByNickname("신규닉네임") } returns false
        every { customerRepository.save(any()) } answers { firstArg() }

        val result = customerService.findOrCreate(
            FindOrCreateCustomerCommand(
                email = "test@example.com",
                provider = SocialProvider.GOOGLE,
                providerId = "provider-1234"
            )
        )

        result.email.address shouldBe "test@example.com"
        result.nickname shouldBe "신규닉네임"
        result.provider shouldBe SocialProvider.GOOGLE
        result.providerId shouldBe "provider-1234"
        result.status shouldBe CustomerStatus.ACTIVE
        result.role shouldBe UserRole.ROLE_CUSTOMER
        result.registeredAt shouldNotBe null

        verify(exactly = 1) { customerRepository.findByProviderAndProviderId(SocialProvider.GOOGLE, "provider-1234") }
        verify(exactly = 2) { RandomNicknameGenerator.generate() }
        verify(exactly = 1) { customerRepository.existsByNickname("충돌닉네임") }
        verify(exactly = 1) { customerRepository.existsByNickname("신규닉네임") }
        verify(exactly = 1) { customerRepository.save(any()) }
        confirmVerified(customerRepository, RandomNicknameGenerator)
    }

    @Test
    fun `마지막 로그인 시간이 현재 시각으로 갱신된다`() {
        val customer = Customer.register(
            email = "test@example.com",
            nickname = "로그인고객",
            provider = SocialProvider.GOOGLE,
            providerId = "provider-5678"
        )

        val beforeUpdate = LocalDateTime.now()

        customerService.updateLastLoginAt(customer)

        val afterUpdate = LocalDateTime.now()
        val updatedLastLoginAt = customer.lastLoginAt

        updatedLastLoginAt shouldNotBe null
        updatedLastLoginAt!!.isBefore(beforeUpdate) shouldBe false
        updatedLastLoginAt.isAfter(afterUpdate) shouldBe false
    }
}
