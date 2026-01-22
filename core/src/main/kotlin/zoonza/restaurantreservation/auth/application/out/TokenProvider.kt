package zoonza.restaurantreservation.auth.application.out

import zoonza.restaurantreservation.shared.UserRole

interface TokenProvider {
    fun generateAccessToken(userId: Long, role: UserRole): String

    fun generateRefreshToken(userId: Long): String

    fun validateToken(token: String)

    fun getUserId(token: String): Long

    fun getUserRole(token: String): String

    fun getJti(token: String): String

    fun getRemainingTime(token: String): Long
}