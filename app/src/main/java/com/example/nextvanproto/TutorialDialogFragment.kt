package com.example.nextvanproto

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.DialogFragment

class TutorialDialogFragment : DialogFragment() {

    private val tutorialImages = listOf(
        R.drawable.tutorial_1,
        R.drawable.tutorial_2,
        R.drawable.tutorial_3,
        R.drawable.tutorial_4

    )
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // Remove default background
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerTutorial)
        val btnNext = view.findViewById<Button>(R.id.btnNext)
        val btnSkip = view.findViewById<TextView>(R.id.btnSkip)

        // Set adapter for ViewPager2
        val adapter = TutorialPagerAdapter(tutorialImages)
        viewPager.adapter = adapter

        // Handle button text change when reaching the last step
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == tutorialImages.size - 1) {
                    btnNext.text = "Got It"  // Change button text on last step
                } else {
                    btnNext.text = "Next"
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < tutorialImages.size - 1) {
                viewPager.currentItem += 1
            } else {
                saveTutorialShown()  // Save tutorial state
                dismiss() // Close tutorial when finished
            }
        }

        btnSkip.setOnClickListener {
            saveTutorialShown()
            dismiss()
        }
    }

    private fun saveTutorialShown() {
        val sharedPref = requireActivity().getSharedPreferences("NextVanPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("tutorial_shown", true)
            apply()
        }
    }
}
