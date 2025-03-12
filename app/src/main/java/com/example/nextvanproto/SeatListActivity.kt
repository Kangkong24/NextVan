package com.example.nextvanproto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nextvanproto.SessionManager.adultCount
import com.example.nextvanproto.SessionManager.childCount
import com.example.nextvanproto.SessionManager.departDate
import com.example.nextvanproto.SessionManager.returnDate
import com.example.nextvanproto.databinding.ActivitySeatListBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SeatListActivity : AppCompatActivity() {
    private lateinit var webSocket: WebSocket
    private val apiService = RetrofitClient.instance
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var seatAdapter: SeatListAdapter
    private val seatList = mutableListOf<Seat>()
    private lateinit var progressBar: ProgressBar

    // Route data variables
    private var routeId: Int = -1
    private var companyId: Int = -1
    private var companyLogo: String = ""
    private var companyName: String = ""
    private var arriveTime: String = ""
    private var date: String = ""
    private var fromLocation: String = ""
    private var toLocation: String = ""
    private var pricePerSeat: Double = 0.0
    private var numberSeat: Int = 0
    private var reservedSeats: String = ""
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager.init(this)
        setupWebSocket()
        getIntentExtra()
        setVariable()
        initSeatList()
        progressBar = findViewById(R.id.progressBar)

        binding.btnConfirmSeats.setOnClickListener {
            val selectedSeats =
                seatList.filter { it.status == Seat.SeatStatus.SELECTED }.map { it.name }

            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select seat accordingly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reserveRequest = ReserveSeatsRequest(routeId, selectedSeats)
            val reserveCall = apiService.reserveSeats(reserveRequest)
            binding.loadingOverlay.visibility = View.VISIBLE

            reserveCall.enqueue(object : retrofit2.Callback<ReserveSeatsResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ReserveSeatsResponse>,
                    response: retrofit2.Response<ReserveSeatsResponse>
                ) {
                    binding.loadingOverlay.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Log.d("SeatReservation", "Seat reserved successfully.")

                        // Proceed to book the ticket only if seat reservation is successful
                        bookTicket(selectedSeats)
                    } else {
                        Toast.makeText(
                            this@SeatListActivity,
                            "Failed to reserve seats",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ReserveSeatsResponse>, t: Throwable) {
                    binding.loadingOverlay.visibility = View.GONE
                    Log.e("SeatReservation", "Network Error: ${t.message}", t)
                    Toast.makeText(this@SeatListActivity, "Network error", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }


        private fun initSeatList() {
            val reservedSeatsList = reservedSeats.split(",").map { it.trim() } // Trim spaces

            seatList.clear() // Prevent duplicate seats

            for (i in 0 until 17) {
                val seatName = "No. ${i + 1}"
                val seatStatus = if (reservedSeatsList.contains(seatName)) {
                    Seat.SeatStatus.UNAVAILABLE
                } else {
                    Seat.SeatStatus.AVAILABLE
                }
                seatList.add(Seat(seatStatus, seatName))
            }

            seatAdapter = SeatListAdapter(seatList, this, object : SeatListAdapter.SelectedSeat {
                override fun Return(selectedNames: String, num: Int) {
                    binding.tvNumSelectedSeat.text = "$num Seat(s) Selected"
                    totalPrice = num * pricePerSeat  // Calculate total price
                    binding.tvPrice.text = "₱${String.format("%.2f", totalPrice)}"
                    binding.tvSelectedSeat.text = selectedNames
                }
            })

            binding.seatRecyclerview.apply {
                val gridLayoutManager = GridLayoutManager(this@SeatListActivity, 3)
                layoutManager = gridLayoutManager
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return 1 // Each item takes 1 span
                    }
                }
                adapter = seatAdapter
            }
        }
    private fun setVariable() {
        binding.imgBackBtn.setOnClickListener { finish() }
    }

    private fun getIntentExtra() {
        val intent = intent

        // Retrieve values from Intent
        routeId = intent.getIntExtra("route_id", -1)
        companyId = intent.getIntExtra("company_id", -1)
        companyLogo = intent.getStringExtra("company_logo") ?: ""
        companyName = intent.getStringExtra("company_name") ?: ""
        arriveTime = intent.getStringExtra("arrive_time") ?: ""
        date = intent.getStringExtra("date") ?: ""
        fromLocation = intent.getStringExtra("from_location") ?: ""
        toLocation = intent.getStringExtra("to_location") ?: ""
        pricePerSeat = intent.getDoubleExtra("price", 0.0)  // Store price per seat
        numberSeat = intent.getIntExtra("number_seat", 0)
        reservedSeats = intent.getStringExtra("reserved_seats") ?: ""
    }


    private fun setupWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        //val request = Request.Builder().url("ws://192.168.152.201:8080").build()
        val request = Request.Builder().url("ws://192.168.100.16:8080").build()
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@SeatListActivity.webSocket = webSocket
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    val json = JSONObject(text)
                    val updatedSeats = json.getJSONArray("reserved_seats")
                    val updatedSeatList = mutableListOf<String>()
                    for (i in 0 until updatedSeats.length()) {
                        updatedSeatList.add(updatedSeats.getString(i))
                    }
                    updateSeatList(updatedSeatList)
                }

            }
        }
        client.newWebSocket(request, listener)
    }

    private fun updateSeatList(updatedSeats: List<String>) {
        for (seat in seatList) {
            if (updatedSeats.contains(seat.name)) {
                seat.status = Seat.SeatStatus.UNAVAILABLE
            }
        }
        seatAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::webSocket.isInitialized) {  // Check if webSocket is initialized
            webSocket.close(1000, "Activity Destroyed")
        }
    }

    // Function to book ticket after successful seat reservation
    private fun bookTicket(selectedSeats: List<String>) {
        val userId = SessionManager.userId // Retrieve logged-in user ID

        if (userId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val bookRequest = BookTicketRequest(
            userId,
            routeId,
            companyId,
            selectedSeats,
            totalPrice,
            departDate ?: "",
            returnDate ?: ""
        )

        val requestBody = Gson().toJson(bookRequest)
        Log.e("TicketBooking", "Sending JSON: $requestBody")
        Log.d("BookTicket", "Sending data: $bookRequest")

        val bookCall = apiService.bookTicket(bookRequest)
        binding.loadingOverlay.visibility = View.VISIBLE

        bookCall.enqueue(object : retrofit2.Callback<BookTicketResponse> {
            override fun onResponse(
                call: retrofit2.Call<BookTicketResponse>,
                response: retrofit2.Response<BookTicketResponse>
            ) {
                binding.loadingOverlay.visibility = View.GONE
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        Log.d("BookTicket", "Booking successful!")
                        Toast.makeText(
                            this@SeatListActivity,
                            "Ticket booked successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Move to TicketDetailActivity after successful booking
                        val intent = Intent(this@SeatListActivity, TicketDetailActivity::class.java)
                        intent.putExtra("selectedSeats", selectedSeats.joinToString(", "))
                        intent.putExtra("totalPrice", totalPrice)
                        intent.putExtra("company_name", companyName)
                        intent.putExtra("company_logo", companyLogo)
                        intent.putExtra("from_location", fromLocation)
                        intent.putExtra("to_location", toLocation)
                        intent.putExtra("date", date)
                        intent.putExtra("arrive_time", arriveTime)
                        intent.putExtra("depart_date", departDate)
                        intent.putExtra("return_date", returnDate)
                        intent.putExtra("adult_count", adultCount)
                        intent.putExtra("child_count", childCount)
                        startActivity(intent)
                    } else {
                        val errorMessage = responseBody?.message ?: response.errorBody()?.string()
                        Log.e("TicketBooking", "Booking failed: ${responseBody?.message}")
                        Toast.makeText(
                            this@SeatListActivity,
                            responseBody?.message ?: "Failed to book ticket $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("TicketBooking", "Unexpected Response: ${response.errorBody()?.string()}")
                    Toast.makeText(this@SeatListActivity, "Failed to book ticket", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: retrofit2.Call<BookTicketResponse>, t: Throwable) {
                binding.loadingOverlay.visibility = View.GONE
                Log.e("TicketBooking", "Network Error: ${t.message}", t)
                Toast.makeText(this@SeatListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


}