package com.example.oauthprovider.core

import arrow.core.Either
import arrow.core.raise.either
import com.example.oauthprovider.user.domain.Email
import com.example.oauthprovider.user.domain.Name
import com.example.oauthprovider.user.domain.Password

inline fun <reified D : Dto, reified R : Request> D.toRequest(): Either<Failure, R> = either {
    val dtoProperties = D::class.members.filterIsInstance<kotlin.reflect.KProperty1<D, *>>()
    val requestConstructor = R::class.constructors.first()

    val arguments = requestConstructor.parameters.map { parameter ->
        val dtoProperty = dtoProperties.find { it.name == parameter.name }
            ?: throw IllegalArgumentException("Missing property for ${parameter.name}")

        val value = dtoProperty.get(this@toRequest)

        when (parameter.type.classifier) {
            Name::class -> Name(value as String).bind()
            Email::class -> Email(value as String).bind()
            Password::class -> Password(value as String).bind()
            else -> throw IllegalArgumentException("Unsupported type: ${parameter.type}")
        }
    }

    requestConstructor.call(*arguments.toTypedArray())
}