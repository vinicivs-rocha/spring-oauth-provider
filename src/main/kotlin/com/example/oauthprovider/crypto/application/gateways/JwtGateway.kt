package com.example.oauthprovider.crypto.application.gateways

import arrow.core.Either
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.crypto.domain.Token

interface JwtGateway {
    fun encode(token: Token): String
    fun decode(encrypted: String): Either<Failure, Token>
}