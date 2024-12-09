package com.example.oauthprovider.validation.api.resolvers

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import com.example.oauthprovider.core.DomainBody
import com.example.oauthprovider.core.DomainField
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.server.ResponseStatusException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

@Component
class DomainBodyResolver(
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(DomainBody::class.java) && parameter.parameterType.kotlin.isData
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("Could not retrieve HttpServletRequest")
        val requestBody =
            objectMapper.readValue(request.reader.readText(), object : TypeReference<Map<String, Any>>() {})
        val parameterClass = parameter.parameterType.kotlin
        val constructor = parameterClass.primaryConstructor
            ?: throw IllegalArgumentException(
                "No primary constructor found for handler parameter"
            )

        return when (val instance = instantiateDomainValidParameter(constructor, parameterClass.memberProperties, requestBody)) {
            is Right -> instance.value
            is Either.Left -> when (instance.value.code) {
                FailureCode.InvalidSetupParameter -> throw IllegalArgumentException(instance.value.message)
                FailureCode.InvalidInputParameter -> throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    instance.value.message
                )

                else -> throw UnknownError(instance.value.message)
            }
        }
    }

    private fun instantiateDomainValidParameter(
        constructor: KFunction<Any>,
        properties: Collection<KProperty1<out Any, *>>,
        requestBody: Map<String, Any>
    ): Either<Failure, Any> = either {
        constructor.callBy(constructor.parameters.associateWith {
            val property = ensureNotNull(properties.find { property -> property.name == it.name }) {
                Failure(
                    code = FailureCode.InvalidSetupParameter,
                    message = "Domain valid constructor parameter ${it.name} not found in properties"
                )
            }
            evaluateValue(property, requestBody).bind()
        })
    }

    private fun evaluateValue(property: KProperty1<out Any, *>, inputValues: Map<String, Any>): Either<Failure, Any?> = either {
        val propertyClass =
            ensureNotNull(property.returnType.classifier) {
                Failure(
                    code = FailureCode.InvalidSetupParameter,
                    message = "Domain valid constructor parameter must be a kotlin's denotable class"
                )
            } as KClass<*>
        val inputValue = inputValues[property.name]
        return when (val annotation = property.findAnnotation<DomainField>()) {
            is DomainField -> validateDomainField(propertyClass, inputValue, annotation.required)
            else -> ensureNotNull(propertyClass.primaryConstructor?.call(inputValue)) {
                Failure(
                    code = FailureCode.InvalidSetupParameter,
                    message = "Request non-domain parameter ${property.name} must have a primary constructor"
                )
            }.right()
        }
    }

    private fun validateDomainField(
        domainClass: KClass<*>,
        inputValue: Any?,
        required: Boolean
    ): Either<Failure, Any?> = either {
        if (inputValue == null && !required) return Right(null)

        val result = ensureNotNull(domainClass.companionObject?.let { companion ->
            val invoke = ensureNotNull(companion.functions.find { it.name == "invoke" }) {
                Failure(
                    code = FailureCode.InvalidSetupParameter,
                    message = "Domain field ${domainClass.simpleName} companion object has no invoke method"
                )
            }

            invoke.call(companion.objectInstance, inputValue)
        }) {
            Failure(
                code = FailureCode.InvalidSetupParameter,
                message = "Domain field ${domainClass.simpleName}'s companion object not found"
            )
        }

        ensure(result is Either<*, *>) {
            Failure(
                code = FailureCode.InvalidSetupParameter,
                message = "Domain field ${domainClass.simpleName} invoke method doesn't return Either"
            )
        }

        return when (result) {
            is Either.Left<*> -> when (result.value) {
                is Failure -> raise(result.value as Failure)
                else -> raise(
                    Failure(
                        code = FailureCode.InvalidSetupParameter,
                        message = "Unknown error due to not using Failure error class"
                    )
                )
            }

            is Right<*> -> result
            else -> throw IllegalArgumentException("Domain field ${domainClass.simpleName} invoke method doesn't return Either")
        }
    }
}