package com.example.nextvanproto

data class Route(
    val id: Int,
    val company_logo: String,
    val company_name: String,
    val arrive_time: String,
    val date: String,
    val from_location: String,
    val from_short: String,
    val number_seat: Int,
    val price: Double,
    val reserved_seats: String,
    val to_location: String,
    val to_short: String
)

