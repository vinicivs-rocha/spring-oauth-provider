package com.example.oauthprovider.auth.api

import com.example.oauthprovider.auth.api.authentications.UserAuthentication
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.log.LogMessage
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component

@Component
class AuthenticationTokenCookieAuthenticationFilter(
    requiresAuthenticationRequestMatcher: RequestMatcher,
    private val authenticationManager: AuthenticationManager,
) : AbstractAuthenticationProcessingFilter(requiresAuthenticationRequestMatcher, authenticationManager) {
    private val securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()
    private var securityContextRepository: SecurityContextRepository = RequestAttributeSecurityContextRepository()
    private var failureHandler: AuthenticationFailureHandler = SimpleUrlAuthenticationFailureHandler("/auth/signIn")

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val token = request?.cookies?.find { it.name == "authenticationToken" }?.value

        return authenticationManager.authenticate(UserAuthentication(user = null, token = token))
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val context = securityContextHolderStrategy.createEmptyContext()
        context.authentication = authResult
        securityContextHolderStrategy.context = context
        securityContextRepository.saveContext(context, request, response)
        if (logger.isDebugEnabled) {
            logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult))
        }
        rememberMeServices.loginSuccess(request, response, authResult)
        if (eventPublisher != null) {
            eventPublisher.publishEvent(InteractiveAuthenticationSuccessEvent(authResult, this.javaClass))
        }
        chain?.doFilter(request, response)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        securityContextHolderStrategy.clearContext()
        logger.trace("Failed to process authentication request", failed)
        logger.trace("Cleared SecurityContextHolder")
        logger.trace("Handling authentication failure")
        rememberMeServices.loginFail(request, response)
        failureHandler.onAuthenticationFailure(request, response, failed)
    }
}