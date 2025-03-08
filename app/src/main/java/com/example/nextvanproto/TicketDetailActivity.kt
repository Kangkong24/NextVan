package com.example.nextvanproto

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nextvanproto.databinding.ActivityTicketDetailBinding


class TicketDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val selectedSeats = intent.getStringExtra("selectedSeats") ?: ""
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val companyName = intent.getStringExtra("company_name") ?: ""
        val companyLogo = intent.getStringExtra("company_logo")?: ""
        val fromLocation = intent.getStringExtra("from_location") ?: ""
        val toLocation = intent.getStringExtra("to_location") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val arriveTime = intent.getStringExtra("arrive_time") ?: ""
        val departureDate = intent.getStringExtra("depart_date")?: ""
        val returnDate = intent.getStringExtra("return_date")?: ""
        val adultCount = intent.getIntExtra("adult_count", 0)
        val childCount = intent.getIntExtra("child_count", 0)



        binding.apply {
            // Load the company logo using Glide
            Glide.with(this@TicketDetailActivity)
                .load(companyLogo)
                .into(imageView5)

            tvCompanyName.text = companyName


            // Route Information
            fromProvince.text = fromLocation
            toProvince.text = toLocation
            textView19.text = arriveTime
            textView22.text = fromLocation
            textView25.text = toLocation
            textView20.text = departureDate
            tvReturnDate.text = returnDate

            // Seat Information
            tvSeatNum.text = selectedSeats
            tvPrice.text = "₱${String.format("%.2f", totalPrice)}"

            tvAdultCount.text = "Adult: $adultCount"
            tvChildCount.text = "Child: $childCount"

            tvBarcodeNum.text = "TKT-${System.currentTimeMillis()}"
        }


        val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val tvUsername = findViewById<TextView>(R.id.user_name)
        tvUsername.text = userName

    }
}