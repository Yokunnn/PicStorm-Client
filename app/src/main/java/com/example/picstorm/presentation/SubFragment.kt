package com.example.picstorm.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentSplashBinding
import com.example.picstorm.databinding.FragmentSubBinding
import com.example.picstorm.domain.TokenStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubFragment : Fragment() {

    private lateinit var binding: FragmentSubBinding
    private lateinit var tokenStorage: TokenStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeToken()
    }

    fun initBottomNav() {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_searchFragment)
        }
        binding.bottomNav.binding.imageUser.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_loginFragment)
        }
    }

    fun initBottomNavAuthorized() {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_searchFragment)
        }
        binding.bottomNav.binding.imageUser.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_profileFragment)
        }
    }

    fun observeToken() {
        tokenStorage.token.observe(viewLifecycleOwner) { token ->
            if (token.accessToken != "null") {
                var jwt = JWT(token.accessToken)
                if (jwt.isExpired(0)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        tokenStorage.deleteToken()
                    }
                } else {
                    initBottomNavAuthorized()
                    //accessToken = token.accessToken
                }
            } else {
                initBottomNav()
                //accessToken = null
            }
        }
    }
}