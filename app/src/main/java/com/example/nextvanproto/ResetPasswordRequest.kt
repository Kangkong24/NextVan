package com.example.nextvanproto

data class ResetPasswordRequest(
    val email: String,
    val otp: Int,
    val new_password: String
)

