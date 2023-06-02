package com.example.picstorm.domain

import com.example.picstorm.domain.model.Profile
import com.example.picstorm.util.ApiResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(userId: Long): Flow<ApiResult<Profile>>
}