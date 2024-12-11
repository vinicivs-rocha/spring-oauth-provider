package com.example.oauthprovider.user.api.controllers

import com.example.oauthprovider.user.api.requests.SignupRequest
import com.example.oauthprovider.user.application.usecases.CreateUser
import com.example.oauthprovider.validation.api.annotations.DomainBody
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthenticationController(
    private val createUser: CreateUser,
) {
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

        println(output.fold({ it.message }, { it.token }))

        return "signup"

//        return when (output) {
//            is Either.Right -> {
//                response.addCookie(Cookie("authenticationToken", output.value.token).apply {
//                    path = "/"
//                    maxAge = 60 * 60 * 24
//                })
//                "home"
//            }
//
//            is Either.Left -> {
//                model.addAttribute("error", output.value.message)
//                "signup"
//            }
//        }
    }
}