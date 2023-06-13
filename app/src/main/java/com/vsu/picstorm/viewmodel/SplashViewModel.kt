package com.vsu.picstorm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.ConfigurationRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepositoryImpl
) : ViewModel() {


    fun loadGlobalConfiguration() {
        viewModelScope.launch { configurationRepository.fetchConfiguration() }
    }
}