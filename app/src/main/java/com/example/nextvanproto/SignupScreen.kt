package com.example.nextvanproto


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupScreen : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etCPassword: EditText
    private lateinit var btnCreate: Button
    private lateinit var tvAlready: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup_screen)

        initializeViews()
        setupButtonListeners()
        handleWindowInsets()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etCPassword = findViewById(R.id.etCPassword)
        tvAlready = findViewById(R.id.tv_already)
        btnCreate = findViewById(R.id.btnCreate)
    }

    private fun setupButtonListeners() {
        btnCreate.setOnClickListener {
            if (validateInputs()) {
                signupUser()
            }
        }

        tvAlready.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }

    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateInputs(): Boolean {
        val fields = listOf(
            etName to "Name",
            etEmail to "Email",
            etPassword to "Password",
            etCPassword to "Confirm Password"
        )

        for ((field, name) in fields) {
            if (field.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "$name is required", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (etPassword.text.toString() != etCPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun signupUser() {
        val request = SignupRequest(
            name = etName.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            password = etPassword.text.toString().trim(),
            confirm_password = etCPassword.text.toString().trim()
        )

        RetrofitClient.instance.signupUser(request).enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                try {
                    val rawResponse = response.errorBody()?.string() ?: response.body()?.toString()

                    if (response.isSuccessful) {
                        response.body()?.let { signupResponse ->
                            if (signupResponse.success) {
                                Toast.makeText(this@SignupScreen, "Signup successful!", Toast.LENGTH_SHORT).show()

                                // Save user login state and details in SharedPreferences
                                signupResponse.user?.let { user ->
                                    saveUserData(user)
                                }

                                // Navigate to the home screen
                                val intent = Intent(this@SignupScreen, LoginScreen::class.java)
                                startActivity(intent)
                                finish() // This ensures SignupScreen is removed from the stack

                            } else {
                                Toast.makeText(this@SignupScreen, signupResponse.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        handleErrorResponse(rawResponse)
                    }
                } catch (e: Exception) {
                    Log.e("PARSE_ERROR", "Failed to parse response: ${e.message}")
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Log.e("NETWORK_ERROR", "Failed to make request: ${t.message}")
                Toast.makeText(this@SignupScreen, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun handleErrorResponse(errorBody: String?) {
        Log.e("SignupScreen", "Error response: $errorBody")
        Toast.makeText(
            this@SignupScreen,
            "Signup failed: ${errorBody ?: "Unknown error"}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun saveUserData(user: UserData) {
        val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userName", user.name)
        editor.putString("userEmail", user.email)
        editor.apply()
    }
}


