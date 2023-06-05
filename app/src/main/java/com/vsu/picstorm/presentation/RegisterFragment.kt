package com.vsu.picstorm.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogAlertBinding
import com.vsu.picstorm.databinding.FragmentDialogConfirmBinding
import com.vsu.picstorm.databinding.FragmentRegisterBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.UserRegister
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.DialogFactory
import com.vsu.picstorm.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var alertBinding: FragmentDialogAlertBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        alertBinding = FragmentDialogAlertBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        dialog = DialogFactory.createAlertDialog(requireContext(), alertBinding)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        observeRegisterResult()
    }

    fun initButtons() {
        binding.buttonReg.setOnClickListener {
            val password = binding.editTextPassword.text.toString().trim()
            val passRepeat = binding.editTextPasswordRepeat.text.toString().trim()
            if (password == passRepeat) {
                registerViewModel.register(
                    UserRegister(
                        binding.editTextLogin.text.toString().trim(),
                        password,
                        binding.editTextEmail.text.toString().trim()
                    )
                )
            } else {
                alertBinding.textView.text = resources.getString(R.string.passwordNotMatch)
                dialog.show()
            }
        }
        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.exitImageButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun observeRegisterResult() {
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS -> {
                    lifecycleScope.launch {
                        val token = result.data!!
                        tokenStorage.saveToken(token)
                        Log.i("Token access", token.accessToken)
                        findNavController().navigate(R.id.action_registerFragment_to_feedFragment)
                    }
                }
                ApiStatus.ERROR -> {
                    alertBinding.textView.text = result.message.toString()
                    dialog.show()
                    binding.buttonReg.isClickable = true
                }
                ApiStatus.LOADING -> {
                    binding.buttonReg.isClickable = false
                }
            }
        }
    }
}