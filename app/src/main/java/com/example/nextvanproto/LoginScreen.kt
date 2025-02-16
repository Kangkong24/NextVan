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

class LoginScreen : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvDont: TextView
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_screen)

        initializeViews()
        setupClickListeners()
        handleWindowInsets()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvDont = findViewById(R.id.tvDont)
        tvForgotPassword = findViewById(R.id.textView5)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            if (validateInputs()) {
                loginUser()
            }
        }

        tvDont.setOnClickListener {
            startActivity(Intent(this, SignupScreen::class.java))
            finish()
        }

        tvForgotPassword.setOnClickListener {
            // Implement forgot password logic
            startActivity(Intent(this, TicketDetailActivity::class.java))
            finish()
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            showError("Email is required")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Invalid email format")
            return false
        }

        if (password.isEmpty()) {
            showError("Password is required")
            return false
        }

        return true
    }

    private fun loginUser() {
        val request = LoginRequest(
            email = etEmail.text.toString().trim(),
            password = etPassword.text.toString().trim()
        )

        RetrofitClient.instance.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (loginResponse.success) {
                            loginResponse.user?.let { user ->
                                handleSuccessfulLogin(user) // Pass user data
                            } ?: showError("User data missing")
                        } else {
                            showError(loginResponse.message ?: "Login failed")
                        }
                    }
                } else {
                    showError("Invalid credentials")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginError", "Network error: ${t.message}")
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun handleSuccessfulLogin(user: UserData) {
        val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userName", user.name)  // Save user's name
        editor.putString("userEmail", user.email)
        editor.apply()

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomeScreen::class.java))
        finish()
    }


    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

