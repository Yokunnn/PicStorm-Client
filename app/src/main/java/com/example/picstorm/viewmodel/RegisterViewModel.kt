package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.RegisterRepositoryImpl
import com.example.picstorm.domain.model.Error
import com.example.picstorm.domain.model.Token
import com.example.picstorm.domain.model.UserRegister
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepositoryImpl
) : ViewModel() {

    val reqState = MutableLiveData<RequestState>()
    val token = MutableLiveData<Token>()

    private lateinit var error: Error

    fun register(userRegister: UserRegister) {
        viewModelScope.launch(Dispatchers.IO) {
            registerRepository.register(
                userRegister.nickname,
                userRegister.password,
                userRegister.email
            ).collect { requestState ->
                when(requestState){
                    is Request.Error -> {
                        error = Error(requestState.message)
                        reqState.postValue(RequestState.ERROR)
                    }
                    is Request.Loading -> {
                        reqState.postValue(RequestState.LOADING)
                    }
                    is Request.Success -> {
                        token.postValue(
                            Token(
                                requestState.data.accessToken,
                                requestState.data.refreshToken
                            )
                        )
                        reqState.postValue(RequestState.SUCCESS)
                    }
                }
            }
        }
    }
}