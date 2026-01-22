package zoonza.restaurantreservation.customer.application.service.command

import zoonza.restaurantreservation.customer.domain.SocialProvider

data class FindOrCreateCustomerCommand(
    val email: String,
    val provider: SocialProvider,
    val providerId: String
)
