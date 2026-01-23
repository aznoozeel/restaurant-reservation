package zoonza.restaurantreservation.auth.application.`in`

interface AuthenticationPort {
    /**
     * 로그아웃
     *
     * @param token Access 토큰
     */
    fun logout(token: String)
}