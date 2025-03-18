package com.example.nextvanproto

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class HelpCenterDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Help Centre")
            .setMessage(getString(R.string.help_center)) // Load from strings.xml
            .setPositiveButton("OK", null)
            .create()
    }
}
