package zoonza.restaurantreservation.customer.application.out

import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.SocialProvider

interface CustomerRepository {
    fun findByProviderAndProviderId(provider: SocialProvider, providerId: String): Customer?

    fun existsByNickname(nickname: String): Boolean

    fun save(customer: Customer): Customer
}