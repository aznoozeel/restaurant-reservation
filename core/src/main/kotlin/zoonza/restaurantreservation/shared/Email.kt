package zoonza.restaurantreservation.shared

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Email(
    @Column(nullable = false, unique = true, length = 255)
    val address: String
) {
    init {
        require(RegexPatterns.emailPattern.matches(address)) { "잘못된 이메일 형식입니다." }
    }
}