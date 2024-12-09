package com.example.oauthprovider.user.domain

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.oauthprovider.core.Failure
import com.example.oauthprovider.core.FailureCode

data class Email private constructor(val value: String) {
    companion object {
        private val emailRegex: Regex
            get() = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")

        operator fun invoke(value: String): Either<Failure, Email> = either {
            ensure(value.isNotBlank()) {
                Failure(
                    code = FailureCode.InvalidInputParameter,
                    message = "Email cannot be blank"
                )
            }
            ensure(emailRegex.matches(value)) {
                Failure(
                    code = FailureCode.InvalidInputParameter,
                    message = "Invalid email format"
                )
            }
            Email(value)
        }
    }
}
