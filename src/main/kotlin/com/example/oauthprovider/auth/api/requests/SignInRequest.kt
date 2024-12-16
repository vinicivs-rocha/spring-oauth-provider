package com.example.oauthprovider.auth.api.requests

import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Password
import com.example.oauthprovider.validation.api.annotations.DomainField

data class SignInRequest(
    @property:DomainField(required = true)
    val email: Email,
    @property:DomainField(required = true)
    val password: Password,
)
