package zoonza.restaurantreservation.adapter.`in`.security.oauth2

import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.stereotype.Component
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.shared.LoginStatus

@Component
class LoginStatusValidator {
    fun validate(customer: Customer) {
        when(customer.checkLoginAvailability()) {
            LoginStatus.SUSPENDED -> "정지된 사용자입니다."
            LoginStatus.WITHDRAWN -> "탈퇴한 사용자입니다."
            else -> return
        }.let { message ->
            throw OAuth2AuthenticationException(message)
        }
    }
}