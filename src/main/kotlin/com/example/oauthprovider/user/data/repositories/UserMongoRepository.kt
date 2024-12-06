package com.example.oauthprovider.user.data.repositories

import com.example.oauthprovider.user.application.repositories.UserRepository
import com.example.oauthprovider.user.data.models.TokenModel
import com.example.oauthprovider.user.data.models.UserModel
import com.example.oauthprovider.user.domain.User
import org.springframework.data.mongodb.core.MongoTemplate
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
}