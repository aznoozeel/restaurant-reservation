package zoonza.restaurantreservation.common

abstract class BusinessException(
    val errorCode: ErrorCode,
    message: String
) : RuntimeException(message)