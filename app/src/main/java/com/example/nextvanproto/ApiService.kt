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

    @POST("book_ticket.php")
    fun bookTicket(@Body request: BookTicketRequest): Call<BookTicketResponse>

    @POST("forgot_password.php")
    fun forgotPassword(@Body request: OtpRequest): Call<ForgotPasswordResponse>

    @POST("verify_otp.php")
    fun verifyOtp(@Body request: VerifyOtpRequest): Call<OtpVerificationResponse>

    @POST("reset_password.php")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ForgotPasswordResponse>

    @GET("get_user_ticket_data.php")
    fun getUserTickets(@Query("user_id") userId: Int): Call<TicketResponse>

    @POST("update_user.php")
    fun updateUser(@Body request: UpdateUserRequest): Call<UpdateUserResponse>

    @POST("submit_feedback.php")
    fun submitFeedback(@Body request: FeedbackRequest): Call<FeedbackResponse>

}


