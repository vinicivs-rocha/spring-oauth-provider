package com.example.oauthprovider.config.api.configuration

import com.example.oauthprovider.auth.api.AuthenticationTokenCookieAuthenticationFilter
import com.example.oauthprovider.auth.api.authenticationproviders.JwtAuthenticationProvider
import com.example.oauthprovider.auth.application.usecases.ValidateAuthenticationToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.security.web.util.matcher.RequestMatchers

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    private val publicUrls = listOf(
        "/auth/signup",
        "/auth/signIn",
        "/favicon.ico"
    )

    @Bean
    fun filterChain(
        http: HttpSecurity,
        authenticationFilter: AuthenticationTokenCookieAuthenticationFilter
    ): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                publicUrls.forEach { authorize(it, permitAll) }
                authorize(requiresAuthentication(), authenticated)
            }
            requestCache {
                requestCache = null
            }
            exceptionHandling {
                authenticationEntryPoint =
                    AuthenticationEntryPoint { _, response, _ -> response.sendRedirect("/auth/signIn") }
            }
        }

        http.addFilterBefore(
            authenticationFilter, UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }

    @Bean
    fun authenticationManager(validateAuthenticationToken: ValidateAuthenticationToken) = ProviderManager(
        JwtAuthenticationProvider(validateAuthenticationToken)
    )

    @Bean
    fun requiresAuthentication(): RequestMatcher {
        return RequestMatchers.allOf(
            *publicUrls.map { NegatedRequestMatcher(AntPathRequestMatcher(it)) }.toTypedArray(),
        )
    }
}