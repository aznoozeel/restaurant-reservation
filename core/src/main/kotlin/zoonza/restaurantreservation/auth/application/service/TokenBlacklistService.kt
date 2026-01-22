package zoonza.restaurantreservation.auth.application.service

import org.springframework.stereotype.Service
import zoonza.restaurantreservation.auth.application.`in`.TokenBlacklistPort
import zoonza.restaurantreservation.auth.application.out.TokenProvider
import zoonza.restaurantreservation.auth.application.out.TokenRepository

@Service
class TokenBlacklistService(
    private val tokenRepository: TokenRepository,
    private val tokenProvider: TokenProvider
) : TokenBlacklistPort {
    override fun checkBlacklist(token: String) {
        val jti = tokenProvider.getJti(token)

        if (tokenRepository.existsInBlacklist(jti)) {
            throw BlacklistedException()
        }
    }
}