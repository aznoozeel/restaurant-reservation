package zoonza.restaurantreservation.config

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {
    @Bean
    @ServiceConnection
    fun postgreSQLContainer(): PostgreSQLContainer<*> =
        PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"))
            .withReuse(true)

    @Bean
    @ServiceConnection
    fun redisContainer(): RedisContainer =
        RedisContainer(DockerImageName.parse("redis:8.4.0-alpine"))
            .withReuse(true)
}