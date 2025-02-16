package com.example.nextvanproto.onboarding_screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.nextvanproto.LoginScreen
import com.example.nextvanproto.R

class ThirdScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        val finish = view.findViewById<Button>(R.id.button3)
        finish.setOnClickListener {
            // Save the onboarding completion state
            saveOnboardingFinished()

            // Navigate to the LoginScreen activity
            val intent = Intent(requireActivity(), LoginScreen::class.java)
            startActivity(intent)
            requireActivity().finish() // Close the current activity
        }

        return view
    }

    private fun saveOnboardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("finished", true) // Save the state
        editor.apply()
    }
}