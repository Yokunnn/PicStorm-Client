package com.example.picstorm.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentLoginBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.domain.model.UserLogin
import com.example.picstorm.util.RequestState
import com.example.picstorm.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()

        observeReqState()
        observeToken()
    }

    fun initButtons() {
        binding.buttonLogin.setOnClickListener {
            loginViewModel.login(
                UserLogin(
                    binding.editTextLogin.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            )
            //findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
        }
        binding.buttonReg.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.exitImageButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
        }
    }

    fun observeReqState() {
        loginViewModel.reqState.observe(viewLifecycleOwner) { requestState ->
            when (requestState) {
                //smth
                RequestState.LOADING -> {
                    Log.i("State", "loading")
                }
                //show dialogfragment
                RequestState.ERROR -> {
                    Log.i("State", "error")
                }
                //nav to feed
                RequestState.SUCCESS -> {
                    Log.i("State", "success")
                }
            }
        }
    }

    fun observeToken() {
        loginViewModel.token.observe(viewLifecycleOwner) { token ->
            when (token) {
                null -> {

                }
                else -> lifecycleScope.launch {
                    tokenStorage.saveToken(token)
                    Log.i("Token", "saved")
                }
            }
        }
    }
}