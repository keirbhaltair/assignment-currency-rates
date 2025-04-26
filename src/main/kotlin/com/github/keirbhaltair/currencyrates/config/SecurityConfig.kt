package com.github.keirbhaltair.currencyrates.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

/**
 * Basic example security configuration.
 */
@Configuration
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/health").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {}
            .build()
    }
}
