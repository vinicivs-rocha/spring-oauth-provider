package com.example.oauthprovider.user.api.requests

import com.example.oauthprovider.core.DomainField
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Name
import com.example.oauthprovider.user.domain.Password

data class SignupRequest(
    @DomainField(required = true)
    val name: Name,
    @DomainField(required = true)
    val email: Email,
    @DomainField(required = true)
    val password: Password,
)