package com.example.nextvanproto

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class TermsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Terms and Conditions")
            .setMessage(getString(R.string.terms_conditions))
            .setPositiveButton("OK", null)
            .create()
    }
}