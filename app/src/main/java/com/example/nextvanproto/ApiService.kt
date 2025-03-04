package com.example.nextvanproto

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {
    @POST("signup.php")
    fun signupUser(@Body request: SignupRequest): Call<SignupResponse>

    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("get_explore_routes.php")
    fun getExploreRoutes(): Call<List<Route>>

    @GET("get_routes.php")
    fun getRoutes(
        @Query("from_location") fromLocation: String,
        @Query("to_location") toLocation: String
    ): Call<List<Route>>

    @GET("get_locations.php")
    fun getLocations(): Call<List<Location>>

    @POST("reserved_seats.php")
    fun reserveSeats(@Body request: ReserveSeatsRequest): Call<ReserveSeatsResponse>
}


