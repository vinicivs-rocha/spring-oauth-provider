package com.example.oauthprovider.user.api.requests

import com.example.oauthprovider.core.Dto
import com.example.oauthprovider.core.Request
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Name
import com.example.oauthprovider.user.domain.Password

data class SignupRequestDto(
    val name: String,
    val email: String,
    val password: String,
) : Dto

data class SignupRequest(
    val name: Name,
    val email: Email,
    val password: Password,
) : Request