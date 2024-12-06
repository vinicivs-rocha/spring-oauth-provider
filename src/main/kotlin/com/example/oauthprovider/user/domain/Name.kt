package com.example.oauthprovider.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.oauthprovider.core.Failure
import com.example.oauthprovider.core.FailureCode

data class Name private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Failure, Name> = either {
            ensure(value.isNotBlank()) { Failure(code = FailureCode.InvalidParameter, "Name cannot be blank") }
            Name(value)
        }
    }
}
