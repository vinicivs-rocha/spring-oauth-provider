package com.example.oauthprovider.either.api.advices

import arrow.core.Either
import com.example.oauthprovider.either.domain.Failure
import com.example.oauthprovider.either.domain.FailureCode
import com.example.oauthprovider.user.api.responses.MessageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice(annotations = [RestController::class])
class EitherControllerAdvice : ResponseEntityExceptionHandler() {
    @ResponseBody
    fun handle(result: Either<Failure, ResponseEntity<*>>): ResponseEntity<*> {
        return when (result) {
            is Either.Left -> choose(failure = result.value)
            is Either.Right -> result.value
        }
    }

    private fun choose(failure: Failure): ResponseEntity<MessageResponse> {
        return when (failure.code) {
            FailureCode.NotFound -> ResponseEntity.notFound().build()
            FailureCode.InvalidInputParameter -> ResponseEntity.badRequest()
                .body(MessageResponse(message = failure.message))

            else -> ResponseEntity.internalServerError().body(MessageResponse(message = failure.message))
        }
    }
}