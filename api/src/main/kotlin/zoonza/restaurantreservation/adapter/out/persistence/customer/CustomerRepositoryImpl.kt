package zoonza.restaurantreservation.adapter.out.persistence.customer

import org.springframework.stereotype.Repository
import zoonza.restaurantreservation.customer.application.out.CustomerRepository
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.SocialProvider

@Repository
class CustomerRepositoryImpl(
    private val customerJpaRepository: CustomerJpaRepository
) : CustomerRepository {
    override fun findByProviderAndProviderId(provider: SocialProvider, providerId: String): Customer? {
        return customerJpaRepository.findByProviderAndProviderId(provider, providerId)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return customerJpaRepository.existsByNickname(nickname)
    }

    override fun save(customer: Customer): Customer {
        return customerJpaRepository.save(customer)
    }
}