package com.example.oauthprovider.user.data.repositories

import arrow.core.Either
import arrow.core.raise.either
import com.example.oauthprovider.crypto.domain.Token
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.user.application.repositories.UserRepository
import com.example.oauthprovider.user.data.models.TokenModel
import com.example.oauthprovider.user.data.models.UserModel
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Name
import com.example.oauthprovider.user.domain.Password
import com.example.oauthprovider.user.domain.User
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Repository
class UserMongoRepository(private val mongoTemplate: MongoTemplate) : UserRepository {
    override fun save(user: User) {
        val newUser = UserModel(
            id = user.id,
            name = user.name,
            email = user.email,
            password = user.password,
            tokens = user.tokens.map {
                TokenModel(
                    it.audience.key,
                    it.subject,
                    Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(it.expiresIn)),
                    it.claims
                )
            }
        )

        mongoTemplate.save(newUser)
    }

    override fun findOne(id: String?): Either<Failure, User?> = either {
        val query = Query()

        if (id != null) {
            query.addCriteria(Criteria.where("id").`is`(id))
        }

        mongoTemplate.findOne(query, UserModel::class.java)?.let { userModel ->
            User(
                _id = UUID.fromString(userModel.id),
                _name = Name(userModel.name).bind(),
                _email = Email(userModel.email).bind(),
                _password = Password(userModel.password).bind(),
                _tokens = userModel.tokens.map {
                    Token(
                        audience = it.audience,
                        subject = it.subject,
                        expiresIn = LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.UTC) - it.expiresAt.toInstant().epochSecond,
                        claims = it.claims
                    ).bind()
                }
            )
        }
    }
}