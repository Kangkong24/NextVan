package com.example.nextvanproto

data class FeedbackRequest(
    val email: String,
    val feedback: String,
    val rating: Float
)
