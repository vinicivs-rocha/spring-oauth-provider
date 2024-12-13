package com.example.oauthprovider.user.application.usecases

import arrow.core.Either
import arrow.core.raise.either
import com.example.oauthprovider.crypto.application.gateways.EncryptionGateway
import com.example.oauthprovider.crypto.application.gateways.JwtGateway
import com.example.oauthprovider.crypto.domain.Token
import com.example.oauthprovider.crypto.domain.TokenAudience
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.user.application.repositories.UserRepository
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Name
import com.example.oauthprovider.user.domain.Password
import com.example.oauthprovider.user.domain.User
import org.springframework.stereotype.Component

@Component
class CreateUser(
    private val jwtGateway: JwtGateway,
    private val userRepository: UserRepository,
    private val encryptionGateway: EncryptionGateway
) {
    data class Output(val token: String, val user: User)

    fun execute(name: Name, email: Email, password: Password): Either<Failure, Output> = either {
        val user = User(
            _name = name,
            _email = email,
            _password = Password(encryptionGateway.hash(password.value)).bind(),
        )
        val token = Token(audience = TokenAudience.Authentication.key, subject = user.id).bind()
        user.addToken(token)

        userRepository.save(user)

        val encodedToken = jwtGateway.encode(token)
        Output(token = encodedToken, user = user)
    }
}