package com.example.oauthprovider.auth.ui

import kotlinx.serialization.Serializable

@Serializable
data class Toast(
    val level: ToastLevel,
    val message: String,
) {
    val color
        get() = level.color
}
