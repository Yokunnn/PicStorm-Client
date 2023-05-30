package com.example.picstorm.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentRegisterBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.domain.model.Token
import com.example.picstorm.domain.model.UserRegister
import com.example.picstorm.util.RequestState
import com.example.picstorm.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()

        observeReqState()
        observeToken()
    }

    fun initButtons() {
        binding.buttonReg.setOnClickListener {
            val ps = binding.editTextPassword.text.toString()
            val psr = binding.editTextPasswordRepeat.text.toString()
            if (ps == psr){
                registerViewModel.register(
                    UserRegister(
                        binding.editTextLogin.text.toString(),
                        ps,
                        binding.editTextEmail.text.toString()
                    )
                )
            } else {
                //show dialogfragment
            }
        }
        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.exitImageButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_feedFragment)
        }
    }

    fun observeReqState() {
        registerViewModel.reqState.observe(viewLifecycleOwner) { requestState ->
            when (requestState) {
                RequestState.LOADING -> {
                    Log.i("State", "loading")
                }
                //show dialogfragment
                RequestState.ERROR -> {
                    Log.i("State", "error")
                }
                RequestState.SUCCESS -> {
                    Log.i("State", "success")
                    findNavController().navigate(R.id.action_registerFragment_to_feedFragment)
                }
            }
        }
    }

    fun observeToken() {
        registerViewModel.token.observe(viewLifecycleOwner) { token ->
            when (token) {
                null -> {

                }
                else -> lifecycleScope.launch {
                    tokenStorage.saveToken(token)
                    Log.i("Token", "saved")
                    Log.i("Token access", token.accessToken)
                    Log.i("Token refresh", token.refreshToken)
                }
            }
        }
    }
}