package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.SubscribeRepositoryImpl
import com.example.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLineViewModel @Inject constructor(
    private val subscribeRepository: SubscribeRepositoryImpl
) : ViewModel() {

    val subResult = MutableLiveData< Pair<Long, ApiResult<Void>>>()

    fun changeSubscribe(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.changeSubscribe(token, userId).collect { result ->
                subResult.postValue(Pair(userId, result))
            }
        }
    }
}