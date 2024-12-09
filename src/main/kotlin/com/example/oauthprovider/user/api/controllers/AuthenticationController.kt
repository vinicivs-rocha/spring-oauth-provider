package com.example.oauthprovider.user.api.controllers

import com.example.oauthprovider.core.DomainValid
import com.example.oauthprovider.user.api.requests.SignupRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/auth")
class AuthenticationController {
    @PostMapping("/signup")
    fun signup(@DomainValid @RequestBody signupRequestDto: SignupRequest) {
    }
}