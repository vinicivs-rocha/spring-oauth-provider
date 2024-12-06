package com.example.oauthprovider.crypto.application.gateways

interface EncryptionGateway {
    fun <T> hash(value: T): String
    fun <T> compare(value: T, hashedValue: String): Boolean
}