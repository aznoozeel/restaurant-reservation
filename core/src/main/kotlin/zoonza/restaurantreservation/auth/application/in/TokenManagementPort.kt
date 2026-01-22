package zoonza.restaurantreservation.auth.application.`in`

import zoonza.restaurantreservation.shared.UserRole

/**
 * 토큰 생성/검증과 정보를 추출하는 primary port
 */
interface TokenManagementPort {
    /**
     * 액세스 토큰을 생성한다.
     *
     * @param userId 유저 ID
     * @param role 유저 Role
     * @return Access Token
     */
    fun generateAccessToken(userId: Long, role: UserRole): String

    /**
     * 리프레시 토큰을 생성한다.
     *
     * @param userId 유저 ID
     * @return Refresh Token
     */
    fun generateRefreshToken(userId: Long): String

    /**
     * 토큰 유효성 검사를 한다.
     *
     * @param token Access/Refresh 토큰
     * @throws ExpiredTokenException
     * @throws InvalidTokenException
     */
    fun validate(token: String)

    /**
     * 토큰에서 userId를 추출한다.
     *
     * @param token Access/Refresh 토큰
     * @return userId
     */
    fun extractUserId(token: String): Long

    /**
     * 토큰에서 user role을 추출한다.
     *
     * @param token Access 토큰
     * @return userRole
     */
    fun extractUserRole(token: String): String
}