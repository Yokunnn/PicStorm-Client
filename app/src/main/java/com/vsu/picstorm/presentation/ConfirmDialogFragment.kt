package com.vsu.picstorm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogConfirmBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(resources.getDrawable(R.color.transparent, null))
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog)
    }

    fun initButtons(){
        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonConfirm.setOnClickListener {
            dialog?.dismiss()
        }
    }
}