package com.example.nextvanproto

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class PrivacyDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Privacy Policy")
            .setMessage(getString(R.string.privacy_policy))
            .setPositiveButton("OK", null)
            .create()
    }
}
