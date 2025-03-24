package com.example.nextvanproto

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nextvanproto.SessionManager.adultCount
import com.example.nextvanproto.SessionManager.childCount
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HomeScreen : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var userViewModel: UserViewModel
    private lateinit var helpBtn: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var tvAdultMinus: ImageView
    private lateinit var tvAdultPlus: ImageView
    private lateinit var tvAdultCount: TextView
    private lateinit var tvChildMinus: ImageView
    private lateinit var tvChildPlus: ImageView
    private lateinit var tvChildCount: TextView
    private lateinit var svFrom: SearchView
    private lateinit var svTo: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: LocationAdapter
    private val locationList = mutableListOf<Location>()
    private var focusedSearchView: SearchView? = null
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        if (shouldShowTutorial()) {
            val tutorialDialog = TutorialDialogFragment()
            tutorialDialog.show(supportFragmentManager, "TutorialDialog")
        }

        tvAdultMinus = findViewById(R.id.tvAdultMinus)
        tvAdultPlus = findViewById(R.id.tvAdultPlus)
        tvAdultCount = findViewById(R.id.tvAdultCount)
        tvChildMinus = findViewById(R.id.tvMinusChild)
        tvChildPlus = findViewById(R.id.tvPlusChild)
        tvChildCount = findViewById(R.id.tvChildCount)
        tvUsername = findViewById(R.id.tvUsername)
        helpBtn = findViewById(R.id.imgHelpBtn)


        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        SessionManager.init(this)
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

        // Observe username LiveData
        userViewModel.username.observe(this, Observer { newUsername ->
            tvUsername.text = newUsername
        })

        // Set initial value from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("userName", "User")
        userViewModel.setUsername(savedUsername ?: "User") // Set initial username

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

        helpBtn.setOnClickListener{
            TutorialDialogFragment().show(supportFragmentManager, "TutorialDialog")
        }

        btnSearch.setOnClickListener {
            val fromLocation = svFrom.query.toString().trim()
            val toLocation = svTo.query.toString().trim()
            val departDate = SessionManager.departDate
            val returnDate = SessionManager.returnDate?.takeIf { it.isNotEmpty() }
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

                // Save selected date in SessionManager
                SessionManager.returnDate = formattedDate
            }, year, month, day)

            // Set the minimum date to today to exclude past dates
            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            // Add "Clear" button
            datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear") { _, _ ->
                etReturnDate.text.clear()  // Clear text field
                SessionManager.returnDate = ""  // Reset SessionManager value
            }

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

                // Save selected date in SessionManager
                SessionManager.departDate = formattedDate
            }, year, month, day)

            // Set the minimum date to today to exclude past dates
            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear") { _, _ ->
                etDepartDate.text.clear()  // Clear text field
                SessionManager.departDate = ""  // Reset SessionManager value
            }

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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel()
                    finishAffinity() // Finish all activities in the stack
                } else {
                    backToast = Toast.makeText(applicationContext, "Press again to exit", Toast.LENGTH_SHORT)
                    backToast.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
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
        val totalCount = adultCount + childCount

        if (isIncrement) {
            // Check if the total count will exceed 17
            if (totalCount < 17) {
                if (isAdult && adultCount < 17) {
                    adultCount++
                } else if (!isAdult && childCount < 17) {
                    childCount++
                }
            }
        } else {
            if (isAdult && adultCount > 1) {
                adultCount--
            } else if (!isAdult && childCount > 0) {
                childCount--
            }
        }
        updateUI()
    }

    private fun updateUI() {
        tvAdultCount.text = "$adultCount Adult"
        tvChildCount.text = "$childCount Child"
    }

    private fun shouldShowTutorial(): Boolean {
        val sharedPref = getSharedPreferences("NextVanPrefs", Context.MODE_PRIVATE)
        return !sharedPref.getBoolean("tutorial_shown", false)  // Show if false
    }

}

