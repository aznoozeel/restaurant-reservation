package zoonza.restaurantreservation.`in`.security.oauth2

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import zoonza.restaurantreservation.customer.application.`in`.CustomerManagementPort
import zoonza.restaurantreservation.customer.application.service.command.FindOrCreateCustomerCommand
import zoonza.restaurantreservation.customer.domain.SocialProvider

@Service
class CustomOAuth2UserService(
    private val loginStatusValidator: LoginStatusValidator,
    private val customerManagementPort: CustomerManagementPort
) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = getSocialProvider(userRequest)

        val oAuth2UserInfo = OAuth2UserInfoFactory.create(provider, oAuth2User.attributes)

        val command = FindOrCreateCustomerCommand(oAuth2UserInfo.email, provider, oAuth2UserInfo.id)
        val customer = customerManagementPort.findOrCreate(command)

        loginStatusValidator.validate(customer)

        return CustomOAuth2User(customer, oAuth2User.attributes)
    }

    private fun getSocialProvider(userRequest: OAuth2UserRequest): SocialProvider {
        return SocialProvider.valueOf(userRequest.clientRegistration.registrationId.uppercase())
    }
}