package com.example.nextvanproto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_result)

        recyclerView = findViewById(R.id.searchView)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRoutes()

        val backBtn = findViewById<ImageView>(R.id.imgBack)

        backBtn.setOnClickListener{
            finish()
        }
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
                    recyclerView.adapter = RouteAdapter(routes)
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
}
