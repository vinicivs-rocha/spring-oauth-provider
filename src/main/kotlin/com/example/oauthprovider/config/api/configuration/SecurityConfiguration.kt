package com.example.oauthprovider.config.api.configuration

import com.example.oauthprovider.auth.api.AuthenticationTokenCookieAuthenticationFilter
import com.example.oauthprovider.auth.api.authenticationproviders.JwtAuthenticationProvider
import com.example.oauthprovider.auth.application.usecases.ValidateAuthenticationToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/auth/signup", permitAll)
                authorize("/auth/signIn", permitAll)
                authorize(anyRequest, authenticated)
            }
        }

        http.addFilterAt(
            AuthenticationTokenCookieAuthenticationFilter(
                requiresAuthentication(),
                authenticationManager,
            ), UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }

    @Bean
    fun authenticationManager(validateAuthenticationToken: ValidateAuthenticationToken) = ProviderManager(
        JwtAuthenticationProvider(validateAuthenticationToken)
    )

    @Bean
    fun requiresAuthentication(): RequestMatcher {
        return NegatedRequestMatcher(AntPathRequestMatcher("/auth/**"))
    }
}