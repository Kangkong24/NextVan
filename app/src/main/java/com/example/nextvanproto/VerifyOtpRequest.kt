package com.example.nextvanproto

data class VerifyOtpRequest(
    val email: String,
    val otp: Int
)

