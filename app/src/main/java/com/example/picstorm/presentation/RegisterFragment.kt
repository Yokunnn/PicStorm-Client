package com.example.picstorm.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentRegisterBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.domain.model.UserRegister
import com.example.picstorm.util.ApiStatus
import com.example.picstorm.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        observeRegisterResult()
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

    fun observeRegisterResult() {
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    lifecycleScope.launch {
                        val token = result.data!!
                        tokenStorage.saveToken(token)
                        Log.i("Token access", token.accessToken)
                        findNavController().navigate(R.id.action_registerFragment_to_feedFragment)
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