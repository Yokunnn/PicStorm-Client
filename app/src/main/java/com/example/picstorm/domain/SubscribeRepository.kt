package com.example.picstorm.domain

import com.example.picstorm.util.Request
import kotlinx.coroutines.flow.Flow

interface SubscribeRepository {
    suspend fun changeSubscribe(token: String?, userId: Long): Flow<Request<Long?>>
}