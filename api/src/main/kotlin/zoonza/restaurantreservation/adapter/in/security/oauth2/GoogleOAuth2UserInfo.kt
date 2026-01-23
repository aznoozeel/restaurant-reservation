package zoonza.restaurantreservation.adapter.`in`.security.oauth2

class GoogleOAuth2UserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {
    override val id: String
        get() = attributes["sub"] as String
    override val email: String
        get() = attributes["email"] as String
}