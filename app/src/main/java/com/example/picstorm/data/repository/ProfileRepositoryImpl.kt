package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.service.ProfileService
import com.example.picstorm.domain.ProfileRepository
import com.example.picstorm.domain.model.Profile
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.RequestUtils.requestFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
) : ProfileRepository {

    override suspend fun getProfile(userId: Long): Flow<ApiResult<Profile>> {
        return requestFlow(
            { profileService.getProfile(userId) },
            { value -> value?.mapToDomain() }
        )
    }
}