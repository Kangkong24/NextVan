package com.example.nextvanproto

data class SignupResponse(
    val success: Boolean,
    val message: String?,
    val user: UserData?
)

