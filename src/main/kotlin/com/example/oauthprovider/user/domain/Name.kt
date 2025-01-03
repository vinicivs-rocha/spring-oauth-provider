package com.example.oauthprovider.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode

data class Name private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Failure, Name> = either {
            ensure(value.isNotBlank()) { Failure(code = FailureCode.InvalidInputParameter, "Name cannot be blank") }
            ensure(value.length >= 3) {
                Failure(
                    code = FailureCode.InvalidInputParameter,
                    "Name must be at least 3 characters long"
                )
            }
            Name(value)
        }
    }
}
