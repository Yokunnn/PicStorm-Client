package com.vsu.picstorm.data.repository

import android.graphics.Bitmap
import com.vsu.picstorm.data.mapper.mapToDomain
import com.vsu.picstorm.data.service.PublicationService
import com.vsu.picstorm.domain.FeedRepository
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.domain.model.enums.DateFilterType
import com.vsu.picstorm.domain.model.enums.ReactionType
import com.vsu.picstorm.domain.model.enums.SortFilterType
import com.vsu.picstorm.domain.model.enums.UserFilterType
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
import com.vsu.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class FeedRepositoryImpl @Inject constructor(
    private val publicationService: PublicationService
) : FeedRepository {

    override suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<ApiResult<Void>> {
        return requestFlow(
            {
                val requestBody =
                    byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
                publicationService.loadPhoto(createAuthHeader(token), requestBody)
            },
            { value -> value }
        )
    }

    override suspend fun getFeed(
        token: String?,
        dateFilterType: DateFilterType,
        sortFilterType: SortFilterType,
        userFilterType: UserFilterType,
        specifiedId: Long?,
        index: Int,
        size: Int
    ): Flow<ApiResult<List<Publication>>> {
        return requestFlow(
            {
                publicationService.getFeed(
                    createAuthHeader(token),
                    dateFilterType.toString(),
                    sortFilterType.toString(),
                    userFilterType.toString(),
                    specifiedId,
                    index,
                    size
                )
            },
            { value -> value?.mapToDomain() }
        )
    }

    override suspend fun getPhoto(publicationId: Long): Flow<ApiResult<Bitmap>> {
        return requestFlow(
            { publicationService.getPublicationPhoto(publicationId) },
            { value -> value?.mapToDomain() }
        )
    }

    override suspend fun banPublication(
        token: String?,
        publicationId: Long
    ): Flow<ApiResult<Void>> {
        return requestFlow(
            { publicationService.banPublication(createAuthHeader(token), publicationId) },
            { null }
        )
    }

    override suspend fun deletePublication(
        token: String?,
        publicationId: Long
    ): Flow<ApiResult<Void>> {
        return requestFlow(
            { publicationService.deletePublication(createAuthHeader(token), publicationId) },
            { null }
        )
    }

    override suspend fun reactPublication(
        token: String?,
        publicationId: Long,
        reactionType: ReactionType?
    ): Flow<ApiResult<ReactionType?>> {
        return requestFlow(
            {
                publicationService.reactPublication(
                    createAuthHeader(token),
                    publicationId,
                    reactionType?.toString()
                )
            },
            { value -> value?.mapToDomain() }
        )
    }
}