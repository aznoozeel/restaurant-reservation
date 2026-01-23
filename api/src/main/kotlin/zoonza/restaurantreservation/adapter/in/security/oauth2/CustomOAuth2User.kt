package zoonza.restaurantreservation.`in`.security.oauth2

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import zoonza.restaurantreservation.customer.domain.Customer

class CustomOAuth2User(
    val customer: Customer,
    private val attributes: Map<String, Any>
) : OAuth2User {
    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(customer.role.name))
    }

    override fun getName(): String {
        return attributes["sub"] as String
    }
}