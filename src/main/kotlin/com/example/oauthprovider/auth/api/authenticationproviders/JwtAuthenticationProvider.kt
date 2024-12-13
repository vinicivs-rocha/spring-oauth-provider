package com.example.oauthprovider.auth.api.authenticationproviders

import arrow.core.Either
import com.example.oauthprovider.auth.api.authenticatioexceptions.TokenAuthenticationException
import com.example.oauthprovider.auth.api.authentications.UserAuthentication
import com.example.oauthprovider.auth.application.usecases.ValidateAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import kotlin.reflect.full.isSubclassOf

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
        return authentication.kotlin.isSubclassOf(Authentication::class) && authentication.kotlin == UserAuthentication::class
    }
}