package com.vsu.picstorm.data.repository

import com.vsu.picstorm.data.service.PublicationService
import com.vsu.picstorm.domain.FeedRepository
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
import com.vsu.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
}