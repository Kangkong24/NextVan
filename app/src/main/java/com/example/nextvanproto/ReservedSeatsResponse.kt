package com.example.nextvanproto

data class ReserveSeatsResponse(
    val status: String,
    val reserved_seats: List<String>
)

