package com.example.oauthprovider

import com.example.oauthprovider.crypto.infra.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class OAuthProviderApplication

fun main(args: Array<String>) {
    runApplication<OAuthProviderApplication>(*args)
}
