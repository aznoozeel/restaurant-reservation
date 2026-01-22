package zoonza.restaurantreservation.auth.application.`in`

/**
 * 토큰 블랙리스트 확인/등록 하는 primary port
 */
interface TokenBlacklistPort {
    /**
     * 블랙리스트에 등록된 토큰인지 확인한다.
     *
     * @param token Access 토큰
     * @throws BlacklistedException
     */
    fun checkBlacklist(token: String)
}