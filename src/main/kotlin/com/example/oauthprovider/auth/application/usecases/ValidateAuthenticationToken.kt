package com.example.oauthprovider.auth.application.usecases

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.example.oauthprovider.crypto.application.gateways.JwtGateway
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode
import com.example.oauthprovider.user.application.repositories.UserRepository
import com.example.oauthprovider.user.domain.User
import org.springframework.stereotype.Component

@Component
class ValidateAuthenticationToken(private val jwtGateway: JwtGateway, private val userRepository: UserRepository) {
    data class Output(val user: User)

    fun execute(token: String?): Either<Failure, Output> = either {
        ensureNotNull(token) {
            Failure(code = FailureCode.InvalidToken, message = "Token is null")
        }

        val decodedToken = jwtGateway.decode(token).bind()
        val user = ensureNotNull(userRepository.findOne(id = decodedToken.subject).bind()) {
            Failure(code = FailureCode.InvalidToken, message = "Token's subject does not exists")
        }

        Output(user)
    }

}