package com.vsu.picstorm.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.picstorm.data.repository.ConfigurationRepositoryImpl
import com.vsu.picstorm.data.repository.FeedRepositoryImpl
import com.vsu.picstorm.data.repository.ProfileRepositoryImpl
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.domain.model.enums.DateFilterType
import com.vsu.picstorm.domain.model.enums.ReactionType
import com.vsu.picstorm.domain.model.enums.SortFilterType
import com.vsu.picstorm.domain.model.enums.UserFilterType
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.AppConstants
import com.vsu.picstorm.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepositoryImpl,
    private val profileRepository: ProfileRepositoryImpl,
    private val configurationRepository: ConfigurationRepositoryImpl,
) : ViewModel() {

    lateinit var loadResult: MutableLiveData<ApiResult<Void>>
    lateinit var feedResult: MutableLiveData<ApiResult<List<Publication>>>
    lateinit var deleteResult: MutableLiveData<Pair<Long, ApiResult<Void>>>
    lateinit var banResult: MutableLiveData<Pair<Long, ApiResult<Void>>>
    lateinit var reactResult: MutableLiveData<Pair<Long, ApiResult<ReactionType?>>>
    lateinit var hasUploadButton: MutableLiveData<Boolean>

    fun init() {
        loadResult = MutableLiveData<ApiResult<Void>>()
        feedResult = MutableLiveData<ApiResult<List<Publication>>>()
        deleteResult = MutableLiveData<Pair<Long, ApiResult<Void>>>()
        banResult = MutableLiveData<Pair<Long, ApiResult<Void>>>()
        reactResult = MutableLiveData<Pair<Long, ApiResult<ReactionType?>>>()
        hasUploadButton = MutableLiveData<Boolean>()
    }

    fun loadPhoto(token: String?, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = ImageCompressor.compress(image, AppConstants.PUBLICATION_PHOTO_MAX_SIZE)
            feedRepository.loadPhoto(token, compressed).collect { result ->
                loadResult.postValue(result)
            }
        }
    }

    suspend fun getAvatar(userId: Long, width: Int): Flow<ApiResult<Bitmap>> {
        return profileRepository.getAvatar(userId, width)
    }

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

    fun deletePublication(token: String?, publicationId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepository.deletePublication(token, publicationId).collect { result ->
                deleteResult.postValue(Pair(publicationId, result))
            }
        }
    }

    fun banPublication(token: String?, publicationId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepository.banPublication(token, publicationId).collect { result ->
                banResult.postValue(Pair(publicationId, result))
            }
        }
    }

    fun reactPublication(token: String?, publicationId: Long, reactionType: ReactionType?) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepository.reactPublication(token, publicationId, reactionType).collect { result ->
                reactResult.postValue(Pair(publicationId, result))
            }
        }
    }

    fun loadConfigValues() {
        hasUploadButton.postValue(configurationRepository.hasUploadPhotoButton)
    }
}