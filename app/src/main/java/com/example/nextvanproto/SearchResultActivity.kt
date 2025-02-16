package com.example.nextvanproto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_result)

        val backBtn = findViewById<ImageView>(R.id.imgBack)

        val nextBtn = findViewById<Button>(R.id.next_btn)



        nextBtn.setOnClickListener{
            val intent = Intent(this, TicketDetailActivity::class.java)
            startActivity(intent)
        }

        backBtn.setOnClickListener{
            finish()
        }
    }
}