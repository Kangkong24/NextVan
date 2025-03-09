package com.example.nextvanproto

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var etOtp: EditText
    private lateinit var btnVerifyOtp: Button
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmitPassword: Button

    private var email: String? = null
    private var otp: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        etEmail = view.findViewById(R.id.etEmailForgotPassword)
        btnSendOtp = view.findViewById(R.id.btnSendOtp)
        etOtp = view.findViewById(R.id.etOtp)
        btnVerifyOtp = view.findViewById(R.id.btnVerifyOtp)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnSubmitPassword = view.findViewById(R.id.btnSubmitPassword)

        // Initially hide OTP and password reset fields
        etOtp.visibility = View.GONE
        btnVerifyOtp.visibility = View.GONE
        etNewPassword.visibility = View.GONE
        etConfirmPassword.visibility = View.GONE
        btnSubmitPassword.visibility = View.GONE

        btnSendOtp.setOnClickListener {
            email = etEmail.text.toString().trim()
            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                sendOtpRequest(email!!)
            }
        }

        btnVerifyOtp.setOnClickListener {
            val otpText = etOtp.text.toString().trim()
            if (otpText.isEmpty()) {
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Email is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            otp = otpText.toInt()
            verifyOtp(email!!, otp!!)
        }


        btnSubmitPassword.setOnClickListener {
            resetPassword()
        }

        return view
    }

    private fun sendOtpRequest(email: String) {
        val request = OtpRequest(email)

        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "OTP sent to email", Toast.LENGTH_LONG)
                            .show()

                        // Show OTP field
                        etOtp.visibility = View.VISIBLE
                        btnVerifyOtp.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            requireContext(),
                            response.body()?.message ?: "Error sending OTP",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun verifyOtp(email: String, otp: Int) {
        val request = VerifyOtpRequest(email, otp) // Send JSON object
        RetrofitClient.instance.verifyOtp(request).enqueue(object : Callback<OtpVerificationResponse> {
            override fun onResponse(call: Call<OtpVerificationResponse>, response: Response<OtpVerificationResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "OTP Verified. Set new password.", Toast.LENGTH_LONG).show()
                    etNewPassword.visibility = View.VISIBLE
                    etConfirmPassword.visibility = View.VISIBLE
                    btnSubmitPassword.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), response.body()?.message ?: "Invalid OTP", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<OtpVerificationResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }



    private fun resetPassword() {
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ResetPasswordRequest(email!!, otp!!, newPassword)

        RetrofitClient.instance.resetPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "Password reset successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), LoginScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), response.body()?.message ?: "Failed to reset password", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}


