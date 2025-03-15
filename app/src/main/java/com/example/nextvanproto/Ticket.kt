package com.example.nextvanproto

data class Ticket(
    val id: Int,
    val user_id: Int,
    val route_id: Int,
    val company_id: Int,
    val selected_seats: String,
    val total_price: String,
    val depart_date: String,
    val return_date: String?,
    val status: String,
    val reference_number: String,
    val company_name: String,
    val from_location: String,
    val to_location: String
)
