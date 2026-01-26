package zoonza.restaurantreservation.customer.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zoonza.restaurantreservation.customer.application.`in`.CustomerManagementPort
import zoonza.restaurantreservation.customer.application.out.CustomerRepository
import zoonza.restaurantreservation.customer.application.service.command.FindOrCreateCustomerCommand
import zoonza.restaurantreservation.customer.domain.Customer
import zoonza.restaurantreservation.customer.domain.CustomerNotFoundException

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
) : CustomerManagementPort {
    @Transactional
    override fun findOrCreate(command: FindOrCreateCustomerCommand): Customer {
        return customerRepository.findByProviderAndProviderId(command.provider, command.providerId)
            ?: customerRepository.save(Customer.register(
                command.email,
                generateNickname(),
                command.provider,
                command.providerId
            ))
    }

    override fun find(userId: Long): Customer {
        return customerRepository.findById(userId)
            ?: throw CustomerNotFoundException()
    }

    @Transactional
    override fun updateLastLoginAt(customer: Customer) {
        customer.updateLastLoginAt()
        customerRepository.save(customer)
    }

    private fun generateNickname(): String {
        return generateSequence { RandomNicknameGenerator.generate() }
            .first { !customerRepository.existsByNickname(it) }
    }
}