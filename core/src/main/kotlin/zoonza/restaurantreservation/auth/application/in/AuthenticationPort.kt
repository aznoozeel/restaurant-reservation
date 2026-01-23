package zoonza.restaurantreservation.auth.application.`in`

interface AuthenticationPort {
    fun logout(token: String)
}