package com.example.oauthprovider.user.api.controllers

import com.example.oauthprovider.user.domain.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class HomeController {
    @GetMapping(value = ["/", "/home"])
    fun home(@AuthenticationPrincipal user: User, model: Model): String {
        model["userName"] = user.name
        return "home"
    }
}