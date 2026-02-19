package com.example.localinformant.core.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.localinformant.R

class CustomInfoDialog(
    private val title: String = "",
    private val message: String = "",
    private val positiveButtonText: String = "",
    private val positiveButtonClick: () -> Unit = {},
    private val negativeButtonText: String = "",
    private val negativeButtonClick: () -> Unit = {}
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (positiveButtonText.isEmpty() && negativeButtonText.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } else if (positiveButtonText.isNotEmpty() && negativeButtonText.isNotEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText) { p0, p1 ->
                    positiveButtonClick.invoke()
                }
                .setNegativeButton(negativeButtonText) { dialog, _ ->
                    negativeButtonClick.invoke()
                }
                .create()
        } else if (positiveButtonText.isNotEmpty() && negativeButtonText.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText) { p0, p1 ->
                    positiveButtonClick.invoke()
                }
                .setNegativeButton(getString(R.string.close)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText) { dialog, _ ->
                    negativeButtonClick.invoke()
                }
                .create()
        }
    }
}