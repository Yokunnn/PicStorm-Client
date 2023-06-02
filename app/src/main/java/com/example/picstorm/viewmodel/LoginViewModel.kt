package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.LoginRepositoryImpl
import com.example.picstorm.domain.model.Token
import com.example.picstorm.domain.model.UserLogin
import com.example.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepositoryImpl
) : ViewModel() {

    val loginResult = MutableLiveData<ApiResult<Token>>()

    fun login(userLogin: UserLogin) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.login(userLogin.nickname, userLogin.password).collect { result ->
                loginResult.postValue(result)
            }
        }
    }
}