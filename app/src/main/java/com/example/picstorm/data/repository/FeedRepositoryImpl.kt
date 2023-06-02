package com.example.picstorm.data.repository

import com.example.picstorm.data.service.PublicationService
import com.example.picstorm.domain.FeedRepository
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.RequestUtils.requestFlow
import com.example.picstorm.util.createAuthHeader
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FeedRepositoryImpl @Inject constructor(
    private val publicationService: PublicationService
) : FeedRepository {

    override suspend fun loadPhoto(token: String?, byteArray: ByteArray): Flow<ApiResult<Void>> {
        return requestFlow(
            { publicationService.loadPhoto(createAuthHeader(token), byteArray) },
            { value -> value }
        )
    }
}