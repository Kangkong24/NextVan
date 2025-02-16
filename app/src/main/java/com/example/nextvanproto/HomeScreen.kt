package com.example.nextvanproto

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class HomeScreen : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)



        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val etReturnDate = findViewById<EditText>(R.id.etReturnDate)
        val etDepartDate = findViewById<EditText>(R.id.etDepartDate)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        // Retrieve user name from SharedPreferences
        val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User") // Default to "User" if not found
        // Display the user name
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        tvUsername.text = userName



        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchResultActivity::class.java)
            startActivity(intent)
        }

        etReturnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                etReturnDate.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        etDepartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                etDepartDate.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.bottom_explorer -> {
                    replaceFragment(ExploreFragment())
                    true
                }
                R.id.bottom_bookmark -> {
                    replaceFragment(BookmarkFragment())
                    true
                }
                R.id.bottom_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.bottom_home -> {
                    // Check if a fragment is currently displayed
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_container)
                    if (currentFragment != null) {
                        // Remove the fragment to show the HomeScreen's original content
                        supportFragmentManager.beginTransaction().remove(currentFragment).commit()
                    }
                    true
                }
                else -> false
            }
        }



    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }


}

