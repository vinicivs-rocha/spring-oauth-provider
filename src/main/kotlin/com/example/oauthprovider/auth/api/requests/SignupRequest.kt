package com.example.oauthprovider.auth.api.requests

import com.example.oauthprovider.validation.api.annotations.DomainField
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Name
import com.example.oauthprovider.user.domain.Password

data class SignupRequest(
    @property:DomainField(required = true)
    val name: Name,
    @property:DomainField(required = true)
    val email: Email,
    @property:DomainField(required = true)
    val password: Password,
)