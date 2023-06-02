package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.RegisterRepositoryImpl
import com.example.picstorm.domain.model.Token
import com.example.picstorm.domain.model.UserRegister
import com.example.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepositoryImpl
) : ViewModel() {

    val registerResult = MutableLiveData<ApiResult<Token>>()

    fun register(userRegister: UserRegister) {
        viewModelScope.launch(Dispatchers.IO) {
            registerRepository.register(userRegister.nickname, userRegister.password, userRegister.email).collect { result ->
                registerResult.postValue(result)
            }
        }
    }
}