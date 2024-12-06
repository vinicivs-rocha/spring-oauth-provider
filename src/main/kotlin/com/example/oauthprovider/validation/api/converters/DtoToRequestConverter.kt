package com.example.oauthprovider.validation.api.converters

import arrow.core.Either
import com.example.oauthprovider.core.Dto
import com.example.oauthprovider.core.Failure
import com.example.oauthprovider.core.Request
import com.example.oauthprovider.core.toRequest
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class DtoToRequestConverter<D : Dto, R : Request> : Converter<D, R> {
    override fun convert(dto: D): R {
        return when (val converted: Either<Failure, R> = dto.toRequest()) {
            is Either.Right -> converted.value
            is Either.Left -> throw IllegalArgumentException(converted.value.message)
        }
    }
}