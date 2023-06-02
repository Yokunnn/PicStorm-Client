package com.vsu.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.SubscriptionRepositoryImpl
import com.vsu.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLineViewModel @Inject constructor(
    private val subscribeRepository: SubscriptionRepositoryImpl
) : ViewModel() {

    val subResult = MutableLiveData<Pair<Long, ApiResult<Long>>>()

    fun changeSubscribe(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.changeSubscribe(token, userId).collect { result ->
                subResult.postValue(Pair(userId, result))
            }
        }
    }
}