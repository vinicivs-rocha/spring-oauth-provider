package com.example.oauthprovider.auth.api.authenticatioexceptions

import org.springframework.security.core.AuthenticationException

class TokenAuthenticationException(message: String) : AuthenticationException(message) {
}