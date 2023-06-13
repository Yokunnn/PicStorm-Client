package com.vsu.picstorm.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.ProfileRepositoryImpl
import com.vsu.picstorm.data.repository.SubscriptionRepositoryImpl
import com.vsu.picstorm.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLineViewModel @Inject constructor(
    private val subscribeRepository: SubscriptionRepositoryImpl,
    private val profileRepository: ProfileRepositoryImpl
) : ViewModel() {

    lateinit var subResult: MutableLiveData<Pair<Long, ApiResult<Long>>>

    fun init() {
        subResult = MutableLiveData<Pair<Long, ApiResult<Long>>>()
    }

    fun changeSubscribe(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.changeSubscribe(token, userId).collect { result ->
                subResult.postValue(Pair(userId, result))
            }
        }
    }

    suspend fun getAvatar(userId: Long, width: Int) : Flow<ApiResult<Bitmap>> {
        return profileRepository.getAvatar(userId, width)
    }
}