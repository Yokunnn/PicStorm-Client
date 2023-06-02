package com.example.picstorm.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picstorm.data.repository.FeedRepositoryImpl
import com.example.picstorm.data.repository.ProfileRepositoryImpl
import com.example.picstorm.domain.model.Profile
import com.example.picstorm.domain.model.enums.UserRole
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepositoryImpl,
    private var feedRepository: FeedRepositoryImpl
) : ViewModel() {

    val profileResult = MutableLiveData<ApiResult<Profile>>()
    val uploadPhotoResult = MutableLiveData<ApiResult<Void>>()
    val uploadAvatarResult = MutableLiveData<ApiResult<Void>>()
    val changeAdminResult = MutableLiveData<ApiResult<UserRole>>()
    val banUserResult = MutableLiveData<ApiResult<UserRole>>()

    fun getProfile(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.getProfile(token, userId).collect { result ->
                profileResult.postValue(result)
            }
        }
    }

    fun uploadAvatar(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = ImageCompressor.compress(image, 100)
            profileRepository.uploadAvatar(token, compressed).collect{result ->
                uploadAvatarResult.postValue(result)
            }
        }
    }

    fun uploadPhoto(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = ImageCompressor.compress(image, 1024)
            feedRepository.loadPhoto(token, compressed).collect{result ->
                uploadPhotoResult.postValue(result)
            }
        }
    }

    fun changeAdmin(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.changeAdmin(token, userId).collect { result ->
                changeAdminResult.postValue(result)
            }
        }
    }

    fun banUser(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.banUser(token, userId).collect { result ->
                banUserResult.postValue(result)
            }
        }
    }
}