package com.example.nextvanproto

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackDialogFragment : DialogFragment() {

    private lateinit var etFeedback: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: TextView

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_feedback, container, false)

        etFeedback = view.findViewById(R.id.etFeedback)
        ratingBar = view.findViewById(R.id.ratingBar)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener { dismiss() }

        btnSubmit.setOnClickListener { submitFeedback() }

        return view
    }

    private fun submitFeedback() {
        val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "Unknown") ?: "Unknown"

        val feedbackText = etFeedback.text.toString().trim()
        val rating = ratingBar.rating

        if (feedbackText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter feedback", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FeedbackRequest(userEmail, feedbackText, rating)

        RetrofitClient.instance.submitFeedback(request).enqueue(object : Callback<FeedbackResponse> {
            override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        Toast.makeText(requireContext(), "Feedback submitted!", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Submission failed: ${responseBody?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()} - ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
