package com.example.nextvanproto

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: UserData?
)
