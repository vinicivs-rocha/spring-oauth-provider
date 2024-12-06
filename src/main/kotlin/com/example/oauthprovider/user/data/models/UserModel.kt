package com.example.oauthprovider.user.data.models

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val tokens: List<TokenModel>
)
