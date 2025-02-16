package com.example.nextvanproto

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("signup.php")
    fun signupUser(@Body request: SignupRequest): Call<SignupResponse>

    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}


