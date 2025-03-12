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

    var departDate: String?
        get() = preferences.getString("departDate", null)
        set(value) = preferences.edit().putString("departDate", value).apply()

    var returnDate: String?
        get() = preferences.getString("returnDate", null)
        set(value) = preferences.edit().putString("returnDate", value).apply()

    var adultCount: Int
        get() = preferences.getInt("adultCount", 0)
        set(value) = preferences.edit().putInt("adultCount", value).apply()

    var childCount: Int
        get() = preferences.getInt("childCount", 0)
        set(value) = preferences.edit().putInt("childCount", value).apply()

    // Function to clear selected values when going back to HomeScreen
    fun clearBookingData() {
        preferences.edit().apply {
            remove("departDate")
            remove("returnDate")
            remove("adultCount")
            remove("childCount")
            apply()
        }
    }
}