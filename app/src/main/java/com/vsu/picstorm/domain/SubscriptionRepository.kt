package com.vsu.picstorm.domain

import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    suspend fun changeSubscribe(token: String?, userId: Long): Flow<ApiResult<Long>>
    suspend fun getSubscriptions(token: String?, userId: Long, index: Int, size: Int): Flow<ApiResult<List<UserLine>>>
    suspend fun getSubscribers(token: String?, userId: Long, index: Int, size: Int): Flow<ApiResult<List<UserLine>>>
}