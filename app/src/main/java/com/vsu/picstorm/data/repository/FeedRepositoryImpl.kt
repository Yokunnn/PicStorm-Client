package com.vsu.picstorm.data.repository

import com.vsu.picstorm.data.service.PublicationService
import com.vsu.picstorm.domain.FeedRepository
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
import com.vsu.picstorm.util.createAuthHeader
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