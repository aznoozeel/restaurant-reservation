package zoonza.restaurantreservation.customer.domain

import jakarta.persistence.*
import zoonza.restaurantreservation.shared.Email
import zoonza.restaurantreservation.shared.LoginStatus
import zoonza.restaurantreservation.shared.UserRole
import java.time.LocalDateTime

@Entity
class Customer private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Embedded
    val email: Email,

    @Column(unique = true, nullable = false, length = 15)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: SocialProvider,

    @Column(nullable = false, length = 255)
    val providerId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CustomerStatus,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @Column(nullable = false)
    val registeredAt: LocalDateTime,

    @Column
    var activatedAt: LocalDateTime? = null,

    @Column
    var lastLoginAt: LocalDateTime? = null,

    @Column
    var suspendedAt: LocalDateTime? = null,

    @Column
    var withdrawnAt: LocalDateTime? = null
) {
    companion object {
        fun register(
            email: String,
            nickname: String,
            provider: SocialProvider,
            providerId: String
        ): Customer {
            require(email.isNotBlank()) { "이메일은 필수입니다." }
            require(nickname.isNotBlank()) { "닉네임은 필수입니다."}
            require(providerId.isNotBlank()) { "OAuth2 제공자 ID는 필수입니다." }

            return Customer(
                email = Email(email),
                nickname = nickname,
                provider = provider,
                providerId = providerId,
                status = CustomerStatus.ACTIVE,
                role = UserRole.ROLE_CUSTOMER,
                registeredAt = LocalDateTime.now()
            )
        }
    }

    fun checkLoginAvailability(): LoginStatus {
        return when (status) {
            CustomerStatus.SUSPENDED -> LoginStatus.SUSPENDED
            CustomerStatus.WITHDRAWN -> LoginStatus.WITHDRAWN
            else -> LoginStatus.AVAILABLE
        }
    }

    fun updateLastLoginAt() {
        lastLoginAt = LocalDateTime.now()
    }
}