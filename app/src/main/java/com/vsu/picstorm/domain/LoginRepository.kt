package com.vsu.picstorm.domain

import com.vsu.picstorm.domain.model.Token
import com.vsu.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(Login: String, password: String): Flow<ApiResult<Token>>
}