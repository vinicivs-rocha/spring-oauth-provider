package com.example.oauthprovider.crypto.infra.gateways

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.oauthprovider.core.Failure
import com.example.oauthprovider.core.FailureCode
import com.example.oauthprovider.crypto.application.gateways.JwtGateway
import com.example.oauthprovider.crypto.domain.Token
import com.example.oauthprovider.crypto.infra.config.JwtProperties
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtJjwtGateway(private val jwtProperties: JwtProperties) : JwtGateway {
    private val secret: ByteArray = jwtProperties.secret.toByteArray()
    private val signKey: SecretKey = SecretKeySpec(secret, "HmacSHA256")

    override fun encode(token: Token): String {
        val expiration: Date = Date.from(LocalDateTime.now().plusSeconds(token.expiresIn).toInstant(ZoneOffset.UTC))

        return Jwts.builder().issuer(jwtProperties.issuer).audience().add(token.audienceName).and()
            .subject(token.subject)
            .expiration(expiration).claims(token.claims).signWith(signKey).compact()
    }

    override fun decode(encrypted: String): Either<Failure, Token> = either {
        val parser = Jwts.parser().requireIssuer(jwtProperties.issuer).verifyWith(signKey).build()

        try {
            val decoded = parser.parseSignedClaims(encrypted)
            val payload = decoded.payload
            val expiresIn = payload.expiration.time - Date().time

            ensure(expiresIn > 0) {
                raise(
                    Failure(
                        code = FailureCode.InvalidToken,
                        message = "Token expired"
                    )
                )
            }

            val customClaims =
                payload.filter { it.key != "exp" && it.key != "sub" && it.key != "aud" && it.key != "iss" }

            Token(
                audience = payload.audience.first(),
                subject = payload.subject,
                expiresIn = expiresIn,
                claims = customClaims
            ).bind()
        } catch (exception: Exception) {
            raise(
                Failure(
                    code = FailureCode.InvalidToken,
                    message = exception.message ?: "Invalid token"
                )
            )
        }
    }

}