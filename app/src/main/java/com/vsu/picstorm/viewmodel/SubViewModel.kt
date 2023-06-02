package com.vsu.picstorm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.SubscriptionRepositoryImpl
import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubViewModel @Inject constructor(
    private val subscribeRepository: SubscriptionRepositoryImpl
) : ViewModel() {

    val subsResult = MutableLiveData<ApiResult<List<UserLine>>>()

    fun getSubscribers(token: String?, userId: Long, index: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.getSubscribers(token, userId, index, size).collect { result ->
                subsResult.postValue(result)
            }
        }
    }

    fun getSubscriptions(token: String?, userId: Long, index: Int, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.getSubscriptions(token, userId, index, size).collect { result ->
                subsResult.postValue(result)
            }
        }
    }

}