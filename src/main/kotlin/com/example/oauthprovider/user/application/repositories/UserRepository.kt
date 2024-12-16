package com.example.oauthprovider.user.application.repositories

import arrow.core.Either
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.User

interface UserRepository {
    fun save(user: User)
    fun findOne(id: String? = null, email: Email? = null): Either<Failure, User?>
}