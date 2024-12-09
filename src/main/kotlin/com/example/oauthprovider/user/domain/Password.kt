package com.example.oauthprovider.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode

data class Password private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Failure, Password> = either {
            ensure(value.isNotBlank()) { Failure(code = FailureCode.InvalidInputParameter, "Password cannot be blank") }
            ensure(value.length >= 6) {
                Failure(
                    code = FailureCode.InvalidInputParameter,
                    "Password must be at least 6 characters long"
                )
            }
            Password(value)
        }
    }
}
