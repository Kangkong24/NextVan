package com.example.nextvanproto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nextvanproto.databinding.ActivitySeatListBinding

class SeatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var seatAdapter: SeatListAdapter
    private val seatList = mutableListOf<Seat>()

    // Route data variables
    private var routeId: Int = -1
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

        getIntentExtra()
        setVariable()
        initSeatList()

        // Handle "Proceed" button click
        binding.button4.setOnClickListener {
            val intent = Intent(this, TicketDetailActivity::class.java)
            intent.putExtra("selectedSeats", binding.tvSelectedSeat.text.toString())
            intent.putExtra("totalPrice", totalPrice)  // Pass calculated total price
            startActivity(intent)
        }
    }

    private fun initSeatList() {
        val reservedSeatsList = reservedSeats.split(",") // Convert to list

        for (i in 0 until 18) {
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
                totalPrice = num * pricePerSeat  // Calculate based on price per seat
                binding.tvPrice.text = "₱${String.format("%.2f", totalPrice)}"
                binding.tvSelectedSeat.text = selectedNames
            }
        })

        binding.seatRecyclerview.apply {
            layoutManager = GridLayoutManager(this@SeatListActivity, 3)
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
        companyLogo = intent.getStringExtra("company_logo") ?: ""
        companyName = intent.getStringExtra("company_name") ?: ""
        arriveTime = intent.getStringExtra("arrive_time") ?: ""
        date = intent.getStringExtra("date") ?: ""
        fromLocation = intent.getStringExtra("from_location") ?: ""
        toLocation = intent.getStringExtra("to_location") ?: ""
        pricePerSeat = intent.getDoubleExtra("price", 0.0)  // Store price per seat
        numberSeat = intent.getIntExtra("number_seat", 0)
        reservedSeats = intent.getStringExtra("reserved_seats") ?: ""

        Log.d("SeatListActivity", "Received Intent Data: routeId=$routeId, companyName=$companyName, pricePerSeat=$pricePerSeat, reservedSeats=$reservedSeats")
    }
}