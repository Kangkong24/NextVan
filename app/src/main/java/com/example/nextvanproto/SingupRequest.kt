package com.example.nextvanproto

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirm_password: String
)

