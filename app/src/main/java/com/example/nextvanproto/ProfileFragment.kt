package com.example.nextvanproto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Retrieve user name from SharedPreferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val userEmail = sharedPreferences.getString("userEmail", "Email")

        // Display the user name and Email
        val tvUsername = view.findViewById<TextView>(R.id.tvUserName)
        tvUsername.text = userName

        val tvUserEmail = view.findViewById<TextView>(R.id.tvEmail)
        tvUserEmail.text = userEmail

        btnLogout = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun logoutUser() {
        // Access SharedPreferences and clear login state
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)  // Clear login state
        editor.remove("userName")  // Remove user's name
        editor.remove("userEmail")
        editor.apply()

        // Redirect to LoginScreen
        val intent = Intent(requireActivity(), LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear back stack
        startActivity(intent)
        requireActivity().finish()
    }
}
