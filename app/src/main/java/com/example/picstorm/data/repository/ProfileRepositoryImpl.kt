package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.service.ProfileService
import com.example.picstorm.domain.ProfileRepository
import com.example.picstorm.domain.model.Profile
import com.example.picstorm.domain.model.enums.UserRole
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.RequestUtils.requestFlow
import com.example.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
) : ProfileRepository {

    override suspend fun getProfile(token: String?, userId: Long): Flow<ApiResult<Profile>> {
        return requestFlow(
            { profileService.getProfile(createAuthHeader(token), userId) },
            { value -> value?.mapToDomain() }
        )
    }

    override suspend fun changeAdmin(token: String?, userId: Long): Flow<ApiResult<UserRole>> {
        return requestFlow(
            { profileService.changeAdmin(createAuthHeader(token), userId) },
            { value -> value?.newRole?.let { UserRole.valueOf(it) } }
        )
    }

    override suspend fun banUser(token: String?, userId: Long): Flow<ApiResult<UserRole>> {
        return requestFlow(
            { profileService.banUser(createAuthHeader(token), userId) },
            { value -> value?.newRole?.let { UserRole.valueOf(it) } }
        )
    }

    override suspend fun uploadAvatar(token: String?, byteArray: ByteArray): Flow<ApiResult<Void>> {
        return requestFlow(
            {
                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
                profileService.uploadAvatar(createAuthHeader(token), requestBody)
            },
            { null }
        )
    }
}