package com.example.nextvanproto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var layoutManageAccount: RelativeLayout
    private lateinit var layoutHelpCentre: RelativeLayout
    private lateinit var layoutShareFeedback: RelativeLayout
    private lateinit var layoutInvite: RelativeLayout
    private lateinit var layoutTerms: RelativeLayout
    private lateinit var layoutPrivacy: RelativeLayout
    private lateinit var layoutLogout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User")
        val userEmail = sharedPreferences.getString("userEmail", "Email")

        val tvUsername = view.findViewById<TextView>(R.id.tvUserName)
        tvUsername.text = userName

        val tvUserEmail = view.findViewById<TextView>(R.id.tvEmail)
        tvUserEmail.text = userEmail

        layoutManageAccount = view.findViewById(R.id.layoutManageAccount)
        layoutHelpCentre = view.findViewById(R.id.layoutHelpCenter)
        layoutShareFeedback = view.findViewById(R.id.layoutShareFeedback)
        layoutInvite = view.findViewById(R.id.layoutInvite)
        layoutTerms = view.findViewById(R.id.layoutTerms)
        layoutPrivacy = view.findViewById(R.id.layoutPrivacy)
        layoutLogout = view.findViewById(R.id.layoutLogout)

        setupClickListeners()

        return view
    }

    private fun setupClickListeners() {
        layoutManageAccount.setOnClickListener {
            Toast.makeText(requireContext(), "Manage Account clicked", Toast.LENGTH_SHORT).show()
        }

        layoutHelpCentre.setOnClickListener {
            HelpCenterDialogFragment().show(parentFragmentManager, "HelpCenterDialog")        }

        layoutShareFeedback.setOnClickListener {
            Toast.makeText(requireContext(), "Share Feedback clicked", Toast.LENGTH_SHORT).show()

        }

        layoutInvite.setOnClickListener {
            Toast.makeText(requireContext(), "Invite to NextVan clicked", Toast.LENGTH_SHORT).show()
            shareInviteLink()
        }

        layoutTerms.setOnClickListener {
            val termsDialog = TermsDialogFragment()
            termsDialog.show(parentFragmentManager, "TermsDialog")
        }

        layoutPrivacy.setOnClickListener {
            val privacyDialog = PrivacyDialogFragment()
            privacyDialog.show(parentFragmentManager, "PrivacyDialog")
        }

        layoutLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun shareInviteLink() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Join me on NextVan! Download the app: https://nextvan.app/download")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Invite via"))
    }

    private fun showLogoutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure you want to Logout?")

        builder.setPositiveButton("Yes") { _, _ ->
            logoutUser()
            val intent = Intent(requireContext(), LoginScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun logoutUser() {
        // Access SharedPreferences and clear login state
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)  // Clear login state
        editor.remove("userName")  // Remove user's name
        editor.remove("userEmail")
        editor.apply()

        // Redirect to LoginScreen
        val intent = Intent(requireActivity(), LoginScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear back stack
        startActivity(intent)
        requireActivity().finish()
    }
}