package com.example.nextvanproto

data class TicketResponse(
    val success: Boolean,
    val message: String?,
    val tickets: List<Ticket>?
)