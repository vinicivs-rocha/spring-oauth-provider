package com.example.oauthprovider.auth.api

import com.example.oauthprovider.auth.api.authentications.UserAuthentication
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component

@Component
class AuthenticationTokenCookieAuthenticationFilter(
    requiresAuthenticationRequestMatcher: RequestMatcher,
    private val authenticationManager: AuthenticationManager,
) : AbstractAuthenticationProcessingFilter(requiresAuthenticationRequestMatcher, authenticationManager) {
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val token = request?.cookies?.find { it.name == "authenticationToken" }?.value

        return authenticationManager.authenticate(UserAuthentication(user = null, token = token))
    }
}