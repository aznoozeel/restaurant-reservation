package zoonza.restaurantreservation.out.persistence.customer

import org.springframework.data.jpa.repository.JpaRepository
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.SocialProvider

interface CustomerJpaRepository : JpaRepository<Customer, Long> {
    fun findByProviderAndProviderId(provider: SocialProvider, providerId: String): Customer?

    fun existsByNickname(nickname: String): Boolean
}