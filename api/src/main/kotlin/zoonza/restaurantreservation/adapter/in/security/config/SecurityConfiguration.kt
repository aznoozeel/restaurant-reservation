package zoonza.restaurantreservation.`in`.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import zoonza.restaurantreservation.`in`.security.jwt.JwtAuthenticationFilter
import zoonza.restaurantreservation.`in`.security.jwt.JwtExceptionFilter
import zoonza.restaurantreservation.`in`.security.oauth2.CustomOAuth2UserService
import zoonza.restaurantreservation.`in`.security.oauth2.OAuth2FailureHandler
import zoonza.restaurantreservation.`in`.security.oauth2.OAuth2SuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val oAuth2FailureHandler: OAuth2FailureHandler,

    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtExceptionFilter: JwtExceptionFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize -> authorize
                .anyRequest().permitAll()
            }
            .oauth2Login { oauth2 -> oauth2
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
            }
            .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(jwtAuthenticationFilter, JwtExceptionFilter::class.java)
//            .exceptionHandling { exception ->
//                exception
//                    .authenticationEntryPoint(JwtAuthenticationEntryPoint(objectMapper))
//                    .accessDeniedHandler(JwtAccessDeniedHandler(objectMapper))
//            }

        return http.build()
    }
}