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
                authorize("/auth/signup", permitAll)
                authorize("/auth/signIn", permitAll)
                authorize("/favicon.ico", permitAll)
                authorize(requiresAuthentication(), authenticated)
            }
            requestCache {
                requestCache = null
            }
            exceptionHandling {
                authenticationEntryPoint =
                    AuthenticationEntryPoint { request, response, authException -> response.sendRedirect("/auth/signIn") }
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
            NegatedRequestMatcher(AntPathRequestMatcher("/favicon.ico")),
            NegatedRequestMatcher(AntPathRequestMatcher("/auth/signup")),
            NegatedRequestMatcher(AntPathRequestMatcher("/auth/signIn")),
        )
    }
}