package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.LoginRepositoryImpl
import com.example.picstorm.domain.model.Error
import com.example.picstorm.domain.model.Token
import com.example.picstorm.domain.model.UserLogin
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepositoryImpl
) : ViewModel() {

    val reqState = MutableLiveData<RequestState>()
    val token = MutableLiveData<Token>()

    private lateinit var error: Error

    fun login(userLogin: UserLogin) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.login(userLogin.nickname, userLogin.password).collect { requestState ->
                when (requestState) {
                    is Request.Loading -> {
                        reqState.postValue(RequestState.LOADING)
                    }
                    is Request.Error -> {
                        error = Error(requestState.message)
                        reqState.postValue(RequestState.ERROR)
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