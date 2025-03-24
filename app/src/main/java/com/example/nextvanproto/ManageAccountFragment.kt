package com.example.nextvanproto

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageAccountFragment : Fragment() {

    private lateinit var backBtn: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_account, container, false)

        backBtn = view.findViewById(R.id.imgBackButton)
        etEmail = view.findViewById(R.id.etEmail)
        etUsername = view.findViewById(R.id.etUsername)
        btnSave = view.findViewById(R.id.confirmed_btn)


        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val userEmail = sharedPreferences.getString("userEmail", "Email")

        etEmail.setText(userEmail)
        etUsername.setText(userName)
        etEmail.isEnabled = false

        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_containers, ProfileFragment())
                .commit()
        }

        btnSave.setOnClickListener {
            updateUser(userEmail ?: "")
        }

        return view
    }

    private fun updateUser(email: String) {
        val username = etUsername.text.toString().trim().takeIf { it.isNotEmpty() }

        if (username == null) {
            Toast.makeText(requireContext(), "Enter new username", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateUserRequest(email, username)
        RetrofitClient.instance.updateUser(request).enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val newUsername = etUsername.text.toString().trim()

                    // Update SharedPreferences
                    val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("userName", newUsername).apply()

                    // Update ViewModel so it updates across the app
                    val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
                    userViewModel.setUsername(newUsername)

                    Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), response.body()?.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

