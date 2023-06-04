package com.vsu.picstorm.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.FeedRepositoryImpl
import com.vsu.picstorm.data.repository.ProfileRepositoryImpl
import com.vsu.picstorm.data.repository.SubscriptionRepositoryImpl
import com.vsu.picstorm.domain.model.Profile
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.domain.model.enums.DateFilterType
import com.vsu.picstorm.domain.model.enums.SortFilterType
import com.vsu.picstorm.domain.model.enums.UserFilterType
import com.vsu.picstorm.domain.model.enums.UserRole
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.AppConstants
import com.vsu.picstorm.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepositoryImpl,
    private val feedRepository: FeedRepositoryImpl,
    private val subscribeRepository: SubscriptionRepositoryImpl,
) : ViewModel() {

    val profileResult = MutableLiveData<ApiResult<Profile>>()
    val avatarResult = MutableLiveData<ApiResult<Bitmap>>()
    val uploadPhotoResult = MutableLiveData<ApiResult<Void>>()
    val uploadAvatarResult = MutableLiveData<ApiResult<Void>>()
    val changeAdminResult = MutableLiveData<ApiResult<UserRole>>()
    val banUserResult = MutableLiveData<ApiResult<UserRole>>()
    val subResult = MutableLiveData<ApiResult<Long>>()
    val feedResult = MutableLiveData<ApiResult<List<Publication>>>()

    fun getFeed(
        token: String?,
        dateFilterType: DateFilterType,
        sortFilterType: SortFilterType,
        userFilterType: UserFilterType,
        specifiedId: Long?,
        index: Int,
        size: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepository.getFeed(
                token,
                dateFilterType,
                sortFilterType,
                userFilterType,
                specifiedId,
                index,
                size
            ).collect { result ->
                feedResult.postValue(result)
            }
        }
    }

    suspend fun getPublicationPhoto(publicationId: Long, width: Int): Flow<ApiResult<Bitmap>> {
        return feedRepository.getPhoto(publicationId, width)
    }

    fun getProfile(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.getProfile(token, userId).collect { result ->
                profileResult.postValue(result)
            }
        }
    }

    fun getAvatar(userId: Long, width: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.getAvatar(userId, width).collect { result ->
                avatarResult.postValue(result)
            }
        }
    }

    fun uploadAvatar(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = ImageCompressor.compress(image, AppConstants.AVATAR_PHOTO_MAX_SIZE)
            profileRepository.uploadAvatar(token, compressed).collect{result ->
                uploadAvatarResult.postValue(result)
            }
        }
    }

    fun uploadPhoto(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = ImageCompressor.compress(image, AppConstants.PUBLICATION_PHOTO_MAX_SIZE)
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

    fun changeSubscribe(token: String?, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRepository.changeSubscribe(token, userId).collect { result ->
                subResult.postValue(result)
            }
        }
    }
}