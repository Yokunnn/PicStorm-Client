package com.vsu.picstorm.domain

import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(token: String?, name: String, index: Int, size: Int): Flow<ApiResult<List<UserLine>>>
}