package zoonza.restaurantreservation.`in`.security.oauth2

import zoonza.restaurantreservation.customer.domain.SocialProvider

object OAuth2UserInfoFactory {
    fun create(provider: SocialProvider, attributes: Map<String, Any>): OAuth2UserInfo {
        return when(provider) {
            SocialProvider.GOOGLE -> GoogleOAuth2UserInfo(attributes)
        }
    }
}