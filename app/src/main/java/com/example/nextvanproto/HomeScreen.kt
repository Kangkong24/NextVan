package com.example.nextvanproto

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var tvAdultMinus: TextView
    private lateinit var tvAdultPlus: TextView
    private lateinit var tvAdultCount: TextView
    private lateinit var tvChildMinus: TextView
    private lateinit var tvChildPlus: TextView
    private lateinit var tvChildCount: TextView
    private lateinit var svFrom: SearchView
    private lateinit var svTo: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: LocationAdapter
    private val locationList = mutableListOf<Location>()
    private var focusedSearchView: SearchView? = null // Track focused SearchView

    companion object {
        var adultCount = 0
        var childCount = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        tvAdultMinus = findViewById(R.id.tvAdultMinus)
        tvAdultPlus = findViewById(R.id.tvAdultPlus)
        tvAdultCount = findViewById(R.id.tvAdultCount)
        tvChildMinus = findViewById(R.id.tvMinusChild)
        tvChildPlus = findViewById(R.id.tvPlusChild)
        tvChildCount = findViewById(R.id.tvChildCount)

        // Load session values
        updateUI()

        tvAdultMinus.setOnClickListener { updateCount(isAdult = true, isIncrement = false) }
        tvAdultPlus.setOnClickListener { updateCount(isAdult = true, isIncrement = true) }
        tvChildMinus.setOnClickListener { updateCount(isAdult = false, isIncrement = false) }
        tvChildPlus.setOnClickListener { updateCount(isAdult = false, isIncrement = true) }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val etReturnDate = findViewById<EditText>(R.id.etReturnDate)
        val etDepartDate = findViewById<EditText>(R.id.etDepartDate)
        // Load previous session values
        etDepartDate.setText(SessionManager.departDate ?: "")
        etReturnDate.setText(SessionManager.returnDate ?: "")

        val btnSearch = findViewById<Button>(R.id.btnSearch)

        // Retrieve user name from SharedPreferences
        val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User") // Default to "User" if not found
        // Display the user name
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        tvUsername.text = userName

        svFrom = findViewById(R.id.svFrom)
        svTo = findViewById(R.id.svTo)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        adapter = LocationAdapter(emptyList()) { selectedCity ->
            focusedSearchView?.setQuery(selectedCity, false)
            rvSearchResults.visibility = View.GONE
        }


        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = adapter

        fetchLocations()

        // Attach listeners to both SearchViews
        setupSearchView(svFrom)
        setupSearchView(svTo)


        btnSearch.setOnClickListener {
            val fromLocation = svFrom.query.toString().trim()
            val toLocation = svTo.query.toString().trim()
            val departDate = SessionManager.departDate
            val returnDate = SessionManager.returnDate
            val totalPassengers = (adultCount ?: 0) + (childCount ?: 0)

            // Validate locations
            if (fromLocation.isEmpty() || toLocation.isEmpty()) {
                Toast.makeText(this, "Please select both locations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate departure date
            if (departDate.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a departure date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate return date
            if (returnDate.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a return date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate passenger count
            if (totalPassengers == 0) {
                Toast.makeText(this, "Please select at least one passenger", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // All validations passed, proceed to next activity
            val intent = Intent(this, SearchResultActivity::class.java).apply {
                putExtra("from_location", fromLocation)
                putExtra("to_location", toLocation)
                putExtra("depart_date", departDate)
                putExtra("return_date", returnDate)
                putExtra("adult_count", adultCount)
                putExtra("child_count", childCount)
            }
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

                // Saved selected date in SessionManager
                SessionManager.returnDate = formattedDate
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

                // Save in SessionManager for the session
                SessionManager.departDate = formattedDate
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

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                rvSearchResults.visibility = View.GONE
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                focusedSearchView = searchView
                val query = newText?.trim() ?: ""
                val filteredList = locationList.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                adapter.updateList(filteredList)

                // Show RecyclerView only when there are results
                rvSearchResults.visibility = if (query.isNotEmpty() && filteredList.isNotEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })

        // Set focus listener
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusedSearchView = searchView
                val params = rvSearchResults.layoutParams as ConstraintLayout.LayoutParams
                params.topToBottom = searchView.id
                rvSearchResults.layoutParams = params
                rvSearchResults.requestLayout()
            }
        }
    }

    private fun fetchLocations() {
        val apiService = RetrofitClient.instance
        apiService.getLocations().enqueue(object : Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                if (response.isSuccessful && response.body() != null) {
                    locationList.clear()
                    locationList.addAll(response.body()!!)
                    adapter.updateList(locationList)
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                Toast.makeText(this@HomeScreen, "Error fetching locations: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateCount(isAdult: Boolean, isIncrement: Boolean) {
        if (isAdult) {
            if (isIncrement) adultCount++ else if (adultCount > 0) adultCount--
        } else {
            if (isIncrement) childCount++ else if (childCount > 0) childCount--
        }
        updateUI()
    }

    private fun updateUI() {
        tvAdultCount.text = "$adultCount Adult"
        tvChildCount.text = "$childCount Child"
    }

}

