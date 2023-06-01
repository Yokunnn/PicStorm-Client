package com.example.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.SubscribeRepositoryImpl
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLineViewModel @Inject constructor(
    private val subscribeRepository: SubscribeRepositoryImpl
) : ViewModel() {

    val subReqState = MutableLiveData<Pair<RequestState, Long>>()

    fun changeSubscribe(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.changeSubscribe(token, userId).collect { subId ->
                when (subId) {
                    is Request.Error -> {
                        subReqState.postValue(Pair(RequestState.ERROR, userId))
                    }
                    is Request.Loading -> {
                        subReqState.postValue(Pair(RequestState.LOADING, userId))
                    }
                    is Request.Success -> {
                        subReqState.postValue(Pair(RequestState.SUCCESS, userId))
                    }
                }
            }
        }
    }
}