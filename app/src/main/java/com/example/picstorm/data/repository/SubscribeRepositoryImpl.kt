package com.example.picstorm.data.repository

import com.example.picstorm.data.service.SubscribeService
import com.example.picstorm.domain.SubscribeRepository
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.RequestUtils.requestFlow
import com.example.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeRepositoryImpl @Inject constructor(
    private val subscribeService: SubscribeService
) : SubscribeRepository {

    override suspend fun changeSubscribe(token: String?, userId: Long): Flow<ApiResult<Void>> {
        return requestFlow (
            { subscribeService.changeSubscribe(createAuthHeader(token), userId) },
            { null }
        )
    }
}