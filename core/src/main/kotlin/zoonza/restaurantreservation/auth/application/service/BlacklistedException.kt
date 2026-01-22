package zoonza.restaurantreservation.auth.application.service

import zoonza.restaurantreservation.common.BusinessException
import zoonza.restaurantreservation.common.ErrorCode

class BlacklistedException(
    message: String = "블랙리스트에 등록된 토큰입니다."
) : BusinessException(ErrorCode.BLACKLISTED, message)