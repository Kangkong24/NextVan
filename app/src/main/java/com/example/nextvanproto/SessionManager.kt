package com.example.nextvanproto

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    }

    var userId: Int
        get() = preferences.getInt("userId", -1)  // Changed to "userId"
        set(value) = preferences.edit().putInt("userId", value).apply()

    var departDate: String? = null
    var returnDate: String? = null
    var adultCount: Int = 0
    var childCount: Int = 0
}