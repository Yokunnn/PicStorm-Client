package com.example.picstorm.domain

import com.example.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface SubscribeRepository {
    suspend fun changeSubscribe(token: String?, userId: Long): Flow<ApiResult<Void>>
}