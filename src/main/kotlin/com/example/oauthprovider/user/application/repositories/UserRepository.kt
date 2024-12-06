package com.example.oauthprovider.user.application.repositories

import com.example.oauthprovider.user.domain.User

interface UserRepository {
    fun save(user: User): Unit
}