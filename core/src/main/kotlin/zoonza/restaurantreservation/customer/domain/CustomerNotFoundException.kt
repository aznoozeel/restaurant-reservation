package zoonza.restaurantreservation.customer.domain

import zoonza.restaurantreservation.common.BusinessException
import zoonza.restaurantreservation.common.ErrorCode

class CustomerNotFoundException(
    message: String = "고객 정보를 찾을 수 없습니다."
) : BusinessException(ErrorCode.NOT_FOUND, message)