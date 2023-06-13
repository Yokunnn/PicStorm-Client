package com.vsu.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.RegisterRepositoryImpl
import com.vsu.picstorm.domain.model.Token
import com.vsu.picstorm.domain.model.UserRegister
import com.vsu.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepositoryImpl
) : ViewModel() {

    lateinit var registerResult: MutableLiveData<ApiResult<Token>>

    fun init() {
        registerResult = MutableLiveData<ApiResult<Token>>()
    }

    fun register(userRegister: UserRegister) {
        viewModelScope.launch(Dispatchers.IO) {
            registerRepository.register(userRegister.nickname, userRegister.password, userRegister.email).collect { result ->
                registerResult.postValue(result)
            }
        }
    }
}