package com.example.nextvanproto

data class ReserveSeatsResponse(
    val status: String,        // Example: "success" or "error"
    val reserved_seats: String // Updated list of reserved seats
)

