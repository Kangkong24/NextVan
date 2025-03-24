package com.example.nextvanproto

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nextvanproto.databinding.ActivityTicketDetailBinding
import java.io.File
import java.io.FileOutputStream


class TicketDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val feedbackDialog = FeedbackDialogFragment()
        feedbackDialog.show(supportFragmentManager, "FeedbackDialog")

        // Retrieve data from Intent
        val routeId = intent.getIntExtra("route_id", -1)
        val selectedSeats = intent.getStringExtra("selectedSeats") ?: ""
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val companyName = intent.getStringExtra("company_name") ?: ""
        val companyLogo = intent.getStringExtra("company_logo")?: ""
        val fromLocation = intent.getStringExtra("from_location") ?: ""
        val toLocation = intent.getStringExtra("to_location") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val arriveTime = intent.getStringExtra("arrive_time") ?: ""
        val departureDate = intent.getStringExtra("depart_date")?: ""
        val returnDate = intent.getStringExtra("return_date")?: "N/A"
        val adultCount = intent.getIntExtra("adult_count", 0)
        val childCount = intent.getIntExtra("child_count", 0)
        val referenceNumber = intent.getStringExtra("reference_number")?: ""


        val closeBtn = findViewById<ImageView>(R.id.imgCloseBtn)
        closeBtn.setOnClickListener {
            SessionManager.clearBookingData()
            val intent = Intent(this, HomeScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // Close current activity
        }

        binding.apply {
            // Load the company logo using Glide
            Glide.with(this@TicketDetailActivity)
                .load(companyLogo)
                .into(imageView5)

            tvCompanyName.text = companyName

            // Route Information
            tvVanNo.text = routeId.toString()
            fromProvince.text = fromLocation
            toProvince.text = toLocation
            textView19.text = arriveTime
            tvVanDeparture.text = date
            textView22.text = fromLocation
            textView25.text = toLocation
            textView20.text = departureDate
            tvReturnDate.text = returnDate

            // Seat Information
            tvSeatNum.text = selectedSeats
            tvPrice.text = "₱${String.format("%.2f", totalPrice)}"

            tvAdultCount.text = "Adult: $adultCount"
            tvChildCount.text = "Child: $childCount"

            tvBarcodeNum.text = "${referenceNumber}"

            binding.downloadBtn.setOnClickListener {
                generatePDF()
            }
        }

        val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val tvUsername = findViewById<TextView>(R.id.user_name)
        tvUsername.text = userName

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }
    private fun generatePDF() {
        val view = binding.ticketLayout // Capture only this layout
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val pageCanvas = page.canvas
        pageCanvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        // Get the reference number
        val referenceNumber = intent.getStringExtra("reference_number") ?: "unknown"
        // Sanitize it
        val safeReferenceNumber = referenceNumber.replace(Regex("[^a-zA-Z0-9_-]"), "")

        val pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ticket_$safeReferenceNumber.pdf")

        try {
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            Toast.makeText(this, "PDF saved: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SessionManager.clearBookingData()
    }

    override fun onPause() {
        super.onPause()
        SessionManager.clearBookingData()
    }

    private fun showExitDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Exit Confirmation")
        builder.setMessage("Are you sure you want to go back to the home screen?")

        builder.setPositiveButton("Yes") { _, _ ->
            SessionManager.clearBookingData()
            val intent = Intent(this, HomeScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

}