package com.example.oauthprovider.auth.api.authenticationproviders

import arrow.core.Either
import com.example.oauthprovider.auth.api.authenticatioexceptions.TokenAuthenticationException
import com.example.oauthprovider.auth.api.authentications.UserAuthentication
import com.example.oauthprovider.auth.application.usecases.ValidateAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class JwtAuthenticationProvider(private val validateAuthenticationToken: ValidateAuthenticationToken) :
    AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication? {
        return authentication?.let {
            when (val result = validateAuthenticationToken.execute(it.credentials as? String)) {
                is Either.Left -> throw TokenAuthenticationException(result.value.message)
                is Either.Right -> UserAuthentication(
                    user = result.value.user,
                    token = it.credentials as? String
                )
            }
        }
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if (authentication == null) return false
        return Authentication::class.java.isAssignableFrom(authentication) && String::class.java.isAssignableFrom(
            authentication.getMethod("getCredentials").returnType
        )
    }
}