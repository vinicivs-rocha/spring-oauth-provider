package com.example.oauthprovider.crypto.infra.gateways

import com.example.oauthprovider.crypto.application.gateways.EncryptionGateway
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Component
class EncryptionPBKDF2Gateway(
    private val iterations: Int = 65536,
    private val keyLength: Int = 256,
    private val salt: ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }
) : EncryptionGateway {
    override fun <T> hash(value: T): String {
        return when (value) {
            is String -> {
                val spec = PBEKeySpec(value.toCharArray(), salt, iterations, keyLength)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val hash = factory.generateSecret(spec).encoded
                return Base64.getEncoder().encodeToString(hash)
            }

            else -> value.toString()
        }
    }

    override fun <T> compare(value: T, hashedValue: String): Boolean {
        return hash(value) == hashedValue
    }

}