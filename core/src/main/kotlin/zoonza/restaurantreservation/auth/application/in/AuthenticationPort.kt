package zoonza.restaurantreservation.auth.application.`in`

import zoonza.restaurantreservation.auth.application.service.dto.result.RefreshTokenResult

interface AuthenticationPort {
    /**
     * 로그아웃
     *
     * @param token Access 토큰
     */
    fun logout(token: String)

    /**
     * 토큰 재발급
     *
     * @param refreshToken Refresh 토큰
     * @return RefreshTokenResult (재발급 된 Access, Refresh 토큰)
     */
    fun refreshToken(refreshToken: String): RefreshTokenResult
}