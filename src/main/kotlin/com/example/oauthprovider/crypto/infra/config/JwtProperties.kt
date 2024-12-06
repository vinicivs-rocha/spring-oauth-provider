package com.example.oauthprovider.crypto.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "secret",
    var issuer: String = "issuer"
)