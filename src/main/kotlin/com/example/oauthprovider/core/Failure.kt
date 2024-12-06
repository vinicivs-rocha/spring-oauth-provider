package com.example.oauthprovider.core

enum class FailureCode() {
    NotFound,
    InvalidParameter,
    InvalidToken,
}

data class Failure(val code: FailureCode, val message: String)
