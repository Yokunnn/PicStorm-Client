package com.vsu.picstorm.data.repository

import com.vsu.picstorm.data.mapper.mapToDomain
import com.vsu.picstorm.data.service.SubscriptionService
import com.vsu.picstorm.domain.SubscriptionRepository
import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
import com.vsu.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionService: SubscriptionService
) : SubscriptionRepository {

    override suspend fun changeSubscribe(token: String?, userId: Long): Flow<ApiResult<Long>> {
        return requestFlow(
            { subscriptionService.changeSubscribe(createAuthHeader(token), userId) },
            { value -> value?.id }
        )
    }

    override suspend fun getSubscriptions(
        token: String?,
        userId: Long,
        index: Int,
        size: Int
    ): Flow<ApiResult<List<UserLine>>> {
        return requestFlow(
            { subscriptionService.getSubscriptions(createAuthHeader(token), userId, index, size) },
            { value -> value?.mapToDomain() }
        )
    }

    override suspend fun getSubscribers(
        token: String?,
        userId: Long,
        index: Int,
        size: Int
    ): Flow<ApiResult<List<UserLine>>> {
        return requestFlow(
            { subscriptionService.getSubscribers(createAuthHeader(token), userId, index, size) },
            { value -> value?.mapToDomain() }
        )
    }
}