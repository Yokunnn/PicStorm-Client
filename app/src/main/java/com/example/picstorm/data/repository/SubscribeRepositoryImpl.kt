package com.example.picstorm.data.repository

import com.example.picstorm.data.service.SubscribeService
import com.example.picstorm.domain.SubscribeRepository
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestUtils.requestFlow
import com.example.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeRepositoryImpl @Inject constructor(
    private val subscribeService: SubscribeService
) : SubscribeRepository {

    override suspend fun changeSubscribe(token: String?, userId: Long): Flow<Request<Long?>> {
        return requestFlow {
            val subscribeResponse = subscribeService.changeSubscribe(createAuthHeader(token), userId)
            subscribeResponse.id
        }
    }
}