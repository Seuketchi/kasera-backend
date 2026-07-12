package com.boardinghouse.backend.shared

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain{
        http
            .csrf { it.disable() }                                  // POST/PUT won't be blocked from curl
            .authorizeHttpRequests {it.anyRequest().permitAll() }   // allow alll
        return http.build()
    }
}