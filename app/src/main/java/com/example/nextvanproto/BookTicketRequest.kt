package com.example.nextvanproto

data class BookTicketRequest(
    val user_id: Int,
    val route_id: Int,
    val company_id: Int,
    val selected_seats: List<String>,
    val total_price: Double,
    val depart_date: String,
    val return_date: String,
    val reference_number: String
)
