package com.example.nextvanproto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookmarkFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: BookmarkAdapter
    private var ticketList = mutableListOf<Ticket>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        recyclerView = view.findViewById(R.id.bookmarkView)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        fetchUserTickets()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)  // Improves performance
        recyclerView.clipToPadding = false  // Prevents last item from being cut off
        recyclerView.setPadding(0, 0, 0, 240) // Add padding to bottom
        adapter = BookmarkAdapter(ticketList)
        recyclerView.adapter = adapter
    }


    private fun fetchUserTickets() {
        progressBar.visibility = View.VISIBLE

        val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        if (userId == -1) {
            showError("User not logged in")
            return
        }

        RetrofitClient.instance.getUserTickets(userId).enqueue(object : Callback<TicketResponse> {
            override fun onResponse(call: Call<TicketResponse>, response: Response<TicketResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    ticketList.clear()
                    ticketList.addAll(response.body()?.tickets ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    showError(response.body()?.message ?: "Failed to load tickets")
                }
            }

            override fun onFailure(call: Call<TicketResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
