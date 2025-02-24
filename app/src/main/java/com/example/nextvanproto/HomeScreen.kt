package com.example.nextvanproto

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.util.Calendar

class HomeScreen : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var svFrom: SearchView
    private lateinit var svTo: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: LocationAdapter
    private val locationList = mutableListOf<Location>()

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

        svFrom = findViewById(R.id.svFrom)
        svTo = findViewById(R.id.svTo)
        rvSearchResults = findViewById(R.id.rvSearchResults) // Add this RecyclerView in XML

        adapter = LocationAdapter(locationList) { selectedCity ->
            if (svFrom.hasFocus()) {
                svFrom.setQuery(selectedCity, false)
            } else if (svTo.hasFocus()) {
                svTo.setQuery(selectedCity, false)
            }
            rvSearchResults.visibility = View.GONE
        }


        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = adapter

        fetchLocations()

        // Attach search listeners
        svFrom.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = locationList.filter { it.name.contains(newText ?: "", ignoreCase = true) }
                adapter.updateList(filteredList)
                rvSearchResults.visibility = if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })


        svTo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = locationList.filter { it.name.contains(newText ?: "", ignoreCase = true) }
                adapter.updateList(filteredList)
                rvSearchResults.visibility = if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })



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

    private fun fetchLocations() {
        val apiService = RetrofitClient.instance
        apiService.getLocations().enqueue(object : Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                if (response.isSuccessful && response.body() != null) {
                    locationList.clear()
                    locationList.addAll(response.body()!!)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@HomeScreen, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                Toast.makeText(this@HomeScreen, "Error fetching locations: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun filterLocations(query: String?) {
        val filteredList = locationList.filter { it.name.contains(query ?: "", ignoreCase = true) }
        adapter.updateList(filteredList)
    }


}

