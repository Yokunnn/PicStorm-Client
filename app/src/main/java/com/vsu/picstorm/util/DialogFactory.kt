package com.vsu.picstorm.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogAlertBinding
import com.vsu.picstorm.databinding.FragmentDialogConfirmBinding
import com.vsu.picstorm.databinding.FragmentDialogPhotoLoadBinding

class DialogFactory {

    companion object {

        fun createAlertDialog(context: Context, alertBinding: FragmentDialogAlertBinding): Dialog {
            val dialog = AlertDialog.Builder(context)
                .setView(alertBinding.root)
                .create()
            dialog.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.color.transparent,
                    null
                )
            )
            alertBinding.buttonOk.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun createConfirmDialog(
            context: Context,
            confirmBinding: FragmentDialogConfirmBinding
        ): Dialog {
            val dialog = AlertDialog.Builder(context)
                .setView(confirmBinding.root)
                .create()
            dialog.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.color.transparent,
                    null
                )
            )
            confirmBinding.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun createPhotoLoadDialog(
            context: Context,
            confirmBinding: FragmentDialogPhotoLoadBinding
        ): Dialog {
            val dialog = AlertDialog.Builder(context)
                .setView(confirmBinding.root)
                .create()
            dialog.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.color.transparent,
                    null
                )
            )
            confirmBinding.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }
    }
}