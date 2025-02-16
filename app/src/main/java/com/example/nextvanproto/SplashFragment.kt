package com.example.nextvanproto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        // Delay navigation by 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                !isOnboardingFinished() -> {
                    // Navigate to Onboarding if not finished
                    findNavController().navigate(R.id.navigate_splashFragment_to_onboarding1Fragment)
                }
                isUserLoggedIn() -> {
                    // Navigate to HomeScreen if the user is logged in
                    startActivity(Intent(requireActivity(), HomeScreen::class.java))
                    requireActivity().finish()
                }
                else -> {
                    // Navigate to LoginScreen if not logged in
                    startActivity(Intent(requireActivity(), LoginScreen::class.java))
                    requireActivity().finish()
                }
            }
        }, 3000)

        return view
    }

    private fun isOnboardingFinished(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("finished", false)
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
