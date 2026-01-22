package zoonza.restaurantreservation.shared

object RegexPatterns {
    const val EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

    val emailPattern = EMAIL.toRegex()
}