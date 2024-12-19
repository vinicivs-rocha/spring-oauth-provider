package com.example.oauthprovider.auth.api.controllers

import arrow.core.Either
import com.example.oauthprovider.auth.api.requests.SignInRequest
import com.example.oauthprovider.auth.api.requests.SignupRequest
import com.example.oauthprovider.auth.ui.Toast
import com.example.oauthprovider.auth.ui.ToastLevel
import com.example.oauthprovider.user.application.usecases.AuthenticateUser
import com.example.oauthprovider.user.application.usecases.CreateUser
import com.example.oauthprovider.validation.api.annotations.DomainBody
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
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
    private val authenticateUser: AuthenticateUser,
) {
    @GetMapping("/signIn")
    fun signIn(model: Model): String {
        return "signIn"
    }

    @PostMapping("/signIn")
    fun signIn(@DomainBody signInRequest: SignInRequest, response: HttpServletResponse, model: Model): String? {
        val output = authenticateUser.execute(
            email = signInRequest.email,
            password = signInRequest.password
        )

        return when (output) {
            is Either.Right -> {
                addCookie(response, output.value.token)
                model["userName"] = output.value.user.name
                "home"
            }

            is Either.Left -> {
                val toast = Toast(level = ToastLevel.Danger, message = output.value.message)
                val triggerDetails = mapOf(
                    "makeToast" to mapOf(
                        "color" to toast.color,
                        "message" to output.value.message,
                    )

                )
                response.addHeader("HX-Reswap", "none")
                response.addHeader(
                    "HX-Trigger",
                    Json.encodeToString(triggerDetails)
                )
                response.status = HttpStatus.BAD_REQUEST.value()
                return null
            }
        }
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
    ): String? {
        val output = createUser.execute(
            name = signupRequest.name,
            email = signupRequest.email,
            password = signupRequest.password
        )

        return when (output) {
            is Either.Right -> {
                addCookie(response, output.value.token)
                model["userName"] = output.value.user.name
                "home"
            }

            is Either.Left -> {
                val toast = Toast(level = ToastLevel.Danger, message = output.value.message)
                val triggerDetails = mapOf(
                    "makeToast" to mapOf(
                        "color" to toast.color,
                        "message" to output.value.message,
                    )

                )
                response.addHeader("HX-Reswap", "none")
                response.addHeader(
                    "HX-Trigger",
                    Json.encodeToString(triggerDetails)
                )
                response.status = HttpStatus.BAD_REQUEST.value()
                return null
            }
        }
    }

    private fun addCookie(response: HttpServletResponse, token: String) {
        response.addCookie(Cookie("authenticationToken", token).apply {
            path = "/"
            maxAge = 60 * 60 * 24
            isHttpOnly = true
        })
    }
}