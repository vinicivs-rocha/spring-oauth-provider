package com.example.oauthprovider.user.data.models

import java.util.*

data class TokenModel(
    val audience: String,
    val subject: String,
    val expiresAt: Date,
    val claims: Map<String, Any>
)
