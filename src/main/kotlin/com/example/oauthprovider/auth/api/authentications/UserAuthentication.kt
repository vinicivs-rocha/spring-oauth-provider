package com.example.oauthprovider.auth.api.authentications

import com.example.oauthprovider.user.domain.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class UserAuthentication(
    private val user: User?,
    private val token: String?,
    private val authorities: MutableCollection<GrantedAuthority> = mutableListOf()
) : Authentication {
    private var authenticated = user != null


    override fun getName(): String? {
        return user?.name
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getCredentials(): Any? {
        return token
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return user
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}