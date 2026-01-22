package zoonza.restaurantreservation.customer.application.`in`

import zoonza.restaurantreservation.customer.application.service.command.FindOrCreateCustomerCommand
import zoonza.restaurantreservation.customer.domain.Customer

/**
 * 고객 정보를 조회하거나 갱신하는 primary port
 */
interface CustomerManagementPort {
    /**
     * 고객 정보를 조회한다.
     * 고객 정보가 존재하지 않다면, 생성/저장 한다.
     *
     * @param command.email 고객 이메일
     * @param command.provider OAuth2 제공자
     * @param command.providerId OAuth2 제공자가 부여한 식별자
     * @return Customer
     */
    fun findOrCreate(command: FindOrCreateCustomerCommand): Customer
}