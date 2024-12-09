package com.example.oauthprovider.crypto.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode

enum class TokenAudience(val key: String) {
    Access(key = "access"),
    Authorization(key = "authorization"),
    Authentication(key = "authentication");

    companion object {
        operator fun invoke(key: String): Either<Failure, TokenAudience> = either {
            val type = entries.find { it.key == key }
            ensureNotNull(type) {
                Failure(
                    code = FailureCode.InvalidInputParameter,
                    message = "No token type found with $key"
                )
            }
            type
        }
    }
}

data class Token private constructor(
    val audience: TokenAudience,
    val subject: String,
    val expiresIn: Long,
    val claims: Map<String, Any>,
) {
    val audienceName: String
        get() = audience.key
    
    companion object {
        operator fun invoke(
            audience: String,
            subject: String,
            expiresIn: Long = 60 * 60 * 24,
            claims: Map<String, Any> = emptyMap()
        ): Either<Failure, Token> = either {
            val tokenAudience = TokenAudience(audience).bind()
            Token(tokenAudience, subject, expiresIn, claims)
        }
    }
}
