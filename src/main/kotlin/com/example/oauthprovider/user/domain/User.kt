package com.example.oauthprovider.user.domain

import com.example.oauthprovider.crypto.domain.Token
import java.time.LocalDateTime
import java.util.*

class User(
    private val _id: UUID = UUID.randomUUID(),
    private val _name: Name,
    private val _email: Email,
    private val _password: Password,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    private var _tokens: List<Token> = emptyList(),
) {
    val id: String
        get() = _id.toString()
    val name: String
        get() = _name.value
    val email: String
        get() = _email.value
    val password: String
        get() = _password.value
    val tokens
        get() = _tokens

    fun addToken(token: Token) {
        this._tokens += token
    }

    fun removeToken(token: Token) {
        this._tokens -= token
    }
}