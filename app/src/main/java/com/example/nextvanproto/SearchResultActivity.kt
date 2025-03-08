package com.example.nextvanproto

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvSelectItemPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_result)

        recyclerView = findViewById(R.id.searchView)
        progressBar = findViewById(R.id.progressBar)
        tvSelectItemPrompt = findViewById(R.id.tvSelectItemPrompt)
        tvSelectItemPrompt.visibility = View.VISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRoutes()

        val backBtn = findViewById<ImageView>(R.id.imgBack)
        backBtn.setOnClickListener { finish() }

        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                tvSelectItemPrompt.visibility = View.GONE
                return false
            }
        })
    }

    private fun fetchRoutes() {
        progressBar.visibility = View.VISIBLE

        val fromLocation = intent.getStringExtra("from_location") ?: return
        val toLocation = intent.getStringExtra("to_location") ?: return

        val apiService = RetrofitClient.instance
        apiService.getRoutes(fromLocation, toLocation).enqueue(object : Callback<List<Route>> {
            override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val routes = response.body() ?: emptyList()
                    recyclerView.adapter = RouteAdapter(routes) { selectedRoute ->
                        navigateToSeatList(selectedRoute)
                    }
                } else {
                    Toast.makeText(this@SearchResultActivity, "Failed to load routes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Route>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@SearchResultActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToSeatList(route: Route) {
        val intent = Intent(this, SeatListActivity::class.java).apply {
            putExtra("route_id", route.id)
            putExtra("company_logo", route.company_logo)
            putExtra("company_name", route.company_name)
            putExtra("arrive_time", route.arrive_time)
            putExtra("date", route.date)
            putExtra("from_location", route.from_location)
            putExtra("to_location", route.to_location)
            putExtra("price", route.price)
            putExtra("number_seat", route.number_seat)
            putExtra("reserved_seats", route.reserved_seats)
        }
        startActivity(intent)
    }
}
