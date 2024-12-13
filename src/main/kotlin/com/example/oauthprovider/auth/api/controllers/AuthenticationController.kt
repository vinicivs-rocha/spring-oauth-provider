package com.example.oauthprovider.auth.api.controllers

import arrow.core.Either
import com.example.oauthprovider.user.api.requests.SignupRequest
import com.example.oauthprovider.user.application.usecases.CreateUser
import com.example.oauthprovider.validation.api.annotations.DomainBody
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthenticationController(
    private val createUser: CreateUser,
) {
    @GetMapping("/signIn")
    fun signIn(model: Model): String {
        return "signup"
    }

    @GetMapping("/signup")
    fun signup(model: Model): String {
        return "signup"
    }

    @PostMapping("/signup")
    fun signup(
        @DomainBody signupRequest: SignupRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        val output = createUser.execute(
            name = signupRequest.name,
            email = signupRequest.email,
            password = signupRequest.password
        )

        return when (output) {
            is Either.Right -> {
                response.addCookie(Cookie("authenticationToken", output.value.token).apply {
                    path = "/"
                    maxAge = 60 * 60 * 24
                    isHttpOnly = true
                })
                model["userName"] = output.value.user.name
                "home"
            }

            is Either.Left -> {
                model.addAttribute("error", output.value.message)
                "signup"
            }
        }
    }
}