package com.vsu.picstorm.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogAlertBinding
import com.vsu.picstorm.databinding.FragmentLoginBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.UserLogin
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.DialogFactory
import com.vsu.picstorm.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var alertBinding: FragmentDialogAlertBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginViewModel.init()
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        alertBinding = FragmentDialogAlertBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        dialog = DialogFactory.createAlertDialog(requireContext(), alertBinding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        observeLoginResult()
    }

    private fun initButtons() {
        binding.buttonLogin.setOnClickListener {
            loginViewModel.login(
                UserLogin(
                    binding.editTextLogin.text.toString().trim(),
                    binding.editTextPassword.text.toString().trim()
                )
            )
        }
        binding.buttonReg.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.exitImageButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    lifecycleScope.launch {
                        tokenStorage.saveToken(result.data!!)
                        findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
                    }
                }
                ApiStatus.ERROR ->   {
                    alertBinding.textView.text = result.message.toString()
                    dialog.show()
                    binding.buttonLogin.isClickable = true
                }
                ApiStatus.LOADING ->  {
                    binding.buttonLogin.isClickable = false
                }
            }
        }
    }
}