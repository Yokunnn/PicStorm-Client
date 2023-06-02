package com.example.picstorm.domain

import com.example.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<ApiResult<Void>>
}