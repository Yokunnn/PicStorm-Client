package com.vsu.picstorm.domain

import com.vsu.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<ApiResult<Void>>
}