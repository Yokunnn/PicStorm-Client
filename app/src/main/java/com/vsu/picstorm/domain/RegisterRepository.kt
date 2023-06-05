package com.vsu.picstorm.domain

import com.vsu.picstorm.domain.model.Token
import com.vsu.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    suspend fun register(login: String, password: String, email: String): Flow<ApiResult<Token>>
}