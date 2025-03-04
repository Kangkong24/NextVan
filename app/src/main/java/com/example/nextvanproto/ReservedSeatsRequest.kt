package com.example.nextvanproto

data class ReserveSeatsRequest(
    val route_id: Int,            // Route ID for the selected trip
    val selected_seats: List<String>  // List of seats the user wants to reserve
)

