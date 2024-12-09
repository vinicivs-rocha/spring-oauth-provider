package com.example.oauthprovider.user.api.controllers

import com.example.oauthprovider.core.DomainBody
import com.example.oauthprovider.user.api.requests.SignupRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController {
    @PostMapping("/signup")
    fun signup(@DomainBody signupRequest: SignupRequest): ResponseEntity<Nothing> {
        println(signupRequest)

        return ResponseEntity.ok().build()
    }
}