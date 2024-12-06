package com.example.oauthprovider.user.api.controllers

import arrow.core.Either
import com.example.oauthprovider.core.Failure
import com.example.oauthprovider.core.toRequest
import com.example.oauthprovider.user.api.requests.SignupRequest
import com.example.oauthprovider.user.api.requests.SignupRequestDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/auth")
class AuthenticationController {
    @PostMapping("/signup")
    fun signup(@RequestBody signupRequestDto: SignupRequestDto) {
        val signupRequest: Either<Failure, SignupRequest> = signupRequestDto.toRequest()
    }
}