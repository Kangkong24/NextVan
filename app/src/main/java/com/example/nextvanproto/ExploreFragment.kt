package com.example.nextvanproto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar // Changed to ProgressBar type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        recyclerView = view.findViewById(R.id.exploreView)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        fetchRoutes() // Changed from loadData() to fetchRoutes()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)  // Improves performance
        recyclerView.clipToPadding = false  // Prevents last item from being cut off
        recyclerView.setPadding(0, 0, 0, 160) // Add padding to bottom
        // Consider adding ItemDecoration or default adapter here
    }

    private fun fetchRoutes() {
        progressBar.visibility = View.VISIBLE

        val apiService = RetrofitClient.instance
        apiService.getExploreRoutes().enqueue(object : Callback<List<Route>> {
            override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val routes = response.body() ?: emptyList()
                    // Use ExploreRouteAdapter instead of RouteAdapter
                    recyclerView.adapter = ExploreRouteAdapter(routes)
                } else {
                    showError("Failed to load routes: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Route>>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}