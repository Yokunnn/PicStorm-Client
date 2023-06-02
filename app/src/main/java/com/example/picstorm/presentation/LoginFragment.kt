package com.example.picstorm.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentLoginBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.domain.model.UserLogin
import com.example.picstorm.util.ApiStatus
import com.example.picstorm.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        observeLoginResult()
    }

    fun initButtons() {
        binding.buttonLogin.setOnClickListener {
            loginViewModel.login(
                UserLogin(
                    binding.editTextLogin.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            )
        }
        binding.buttonReg.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.exitImageButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
        }
    }

    fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    lifecycleScope.launch {
                        tokenStorage.saveToken(result.data!!)
                        findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
                    }
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
                }
            }
        }
    }
}