package com.example.oauthprovider.user.application.usecases

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.example.oauthprovider.crypto.application.gateways.EncryptionGateway
import com.example.oauthprovider.crypto.application.gateways.JwtGateway
import com.example.oauthprovider.crypto.domain.Token
import com.example.oauthprovider.crypto.domain.TokenAudience
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode
import com.example.oauthprovider.user.application.repositories.UserRepository
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Password
import com.example.oauthprovider.user.domain.User
import org.springframework.stereotype.Component

@Component
class AuthenticateUser(
    val userRepository: UserRepository,
    val encryptionGateway: EncryptionGateway,
    val jwtGateway: JwtGateway
) {
    data class Output(val token: String, val user: User)

    fun execute(email: Email, password: Password): Either<Failure, Output> = either {
        val user = ensureNotNull(userRepository.findOne(email = email).bind()) {
            Failure(code = FailureCode.NotFound, message = "User not found by the provided credentials")
        }

        ensure(encryptionGateway.compare(value = password.value, hashedValue = user.password)) {
            Failure(code = FailureCode.NotFound, message = "User not found by the provided credentials")
        }

        val token = Token(audience = TokenAudience.Authentication.key, subject = user.id).bind()

        user.addToken(token)

        userRepository.save(user)

        val encodedToken = jwtGateway.encode(token)

        Output(token = encodedToken, user = user)
    }
}